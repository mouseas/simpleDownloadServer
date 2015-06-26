package com.martincarney.servlet;

import java.io.*;
import java.util.regex.*;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SimpleDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static String BASE_FILE_FOLDER;
	private static Pattern FILE_NAME_REGEX;
	
	/**
	 * Pull the constants from config file(s) (namely web.xml)
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		BASE_FILE_FOLDER = getInitParameter("rootFolder");
		String fileNameRegex = getInitParameter("allowedFileNameRegex");
		if (fileNameRegex != null) {
			FILE_NAME_REGEX = Pattern.compile(fileNameRegex);
		} else {
			FILE_NAME_REGEX = Pattern.compile("([^\\s]+(\\.(?i)([^\\s]+))$)"); // default to *.*
		}
	}

	/**
	 * Handle all GET requests to download a specified file.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = extractRequestedFilename(request);
		
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		System.out.print(ip + " requesteded file \"" + filename + "\"");
		
		File requestedFile = new File(BASE_FILE_FOLDER + filename);
		if (requestedFile.exists() && requestedFile.isFile() && isValidFileName(requestedFile.getName())) {
			System.out.println(" - request successful.");
			
			// get file's mime type, and put that in response (default to bin file type if none)
			String mimeType = getServletContext().getMimeType(requestedFile.getName());
			if (mimeType == null) {
				mimeType = "application/octet-stream"; // default to binary if no type found.
			}
			
			// set up the response headers - mime type, file name file size, etc.
			response.setContentType(mimeType);
			response.setContentLength((int) requestedFile.length());
			response.setHeader("Content-disposition", "attachment; filename=\"" + requestedFile.getName() + "\"");
			
			// send the file's data
			sendFileData(response, requestedFile);
		} else {
			System.out.println(" - request failed.");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested file was not found.");
		}
	}
	
	/**
	 * Grunt work of actually sending byte data
	 * @param response
	 * @param requestedFile
	 * @throws IOException
	 */
	private void sendFileData(HttpServletResponse response, File requestedFile) throws IOException {
		FileInputStream in = null;
		OutputStream out = null;
		
		try {
			// Get the streams
			in = new FileInputStream(requestedFile);
			out = response.getOutputStream();
			
			// set up a buffer and counter
			byte[] buffer = new byte[4096];
			int  bytesRead = -1;
			
			// send the data
			while((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} finally {
			// close the streams.
			in.close();
			out.close();
		}
	}

	/**
	 * Separate the relevant part of the path out from the request
	 * @param request
	 * @return
	 */
	private String extractRequestedFilename(HttpServletRequest request) {
		// get the part of the URI that comes after the servlet's root.
		String filename = request.getRequestURI().substring(request.getContextPath().length());
		
		// trim off any leading '/'
		if (filename.startsWith("/")) {
			filename = filename.substring(1);
		}
		
		// swap url path separators for file system path separators
		filename = filename.replace('/', File.separatorChar);
		
		return filename;
	}

	/**
	 * Thread-safe method to check if a filename is valid.
	 * @param filename
	 * @return
	 */
	private boolean isValidFileName(String filename) {
		Matcher m = FILE_NAME_REGEX.matcher(filename);
		return filename != null && m.matches();
	}

}
