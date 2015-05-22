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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String filename = uri.substring(contextPath.length());
		
		// trim off any leading '/'
		if (filename.startsWith("/")) {
			filename = filename.substring(1);
		}
		
		// swap url path separators for file system path separators
		filename = filename.replaceAll("/", File.pathSeparator);
		
		File requestedFile = new File(BASE_FILE_FOLDER + filename);
		System.out.println(requestedFile.getPath());
		if (requestedFile.isFile() && isValidFileName(requestedFile.getName())) {
			// TODO:
			// get in stream for file
			// get file's mime type, and put that in response (default to bin file type if none)
			// set up the response headers - mime type, file name file size, etc.
			// send the file's data
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested file was not found.");
		}
	}

	private boolean isValidFileName(String filename) {
		Matcher m = FILE_NAME_REGEX.matcher(filename);
		return filename != null && m.matches();
	}

}
