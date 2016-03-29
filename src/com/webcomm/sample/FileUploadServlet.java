package com.webcomm.sample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcomm.vo.UploadRespVo;

public class FileUploadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	/* file save path */
//	private static String FILE_PATH = "C:\\XMPPFiles\\";
	private static String FILE_PATH = "C:\\uploadFile\\";
	
	/*
	 * URL: /upload
	 * doPost() : upload multipart content 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException{
		PrintWriter pw = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		UploadRespVo result = new UploadRespVo();
		
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			result.setMsg("No multipart content found.");
			pw.write(mapper.writeValueAsString(result));
			return;
		}
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			Map<String, List<FileItem>> testMap = upload.parseParameterMap(request);
			System.out.println(testMap.keySet().size());
			List<FileItem> fields = testMap.values().iterator().next();
			Iterator<FileItem> it = fields.iterator();
			if (!it.hasNext()) {
				result.setMsg("No fields found.");
				pw.write(mapper.writeValueAsString(result));
				return;
			}
			while (it.hasNext()) {
				FileItem fileItem = it.next();
				boolean isFormField = fileItem.isFormField();
				if (!isFormField) {		
					/* is Multipart content, save it. */
					File savefile = new File(FILE_PATH + fileItem.getName());
					fileItem.write(savefile);
					
					System.out.println("NAME: " + fileItem.getName()
							+ "CONTENT TYPE: " + fileItem.getContentType()
							+ "SIZE (BYTES): " + fileItem.getSize()
							+ "TO STRING: " + fileItem.toString());
					
					result.setSuccess("Y");
					result.setMsg("save success, file name is " + fileItem.getName());
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			result.setMsg(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg(e.toString());
		}
		pw.write(mapper.writeValueAsString(result));
	}
	
	/*
	 *  URL: /upload?file
	 *  doGet(): get file by name
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String fileName = request.getParameter("file");

		File targetFile = new File(FILE_PATH + fileName);
		if (targetFile.exists() && targetFile.canRead()) {

			FileInputStream fis = new FileInputStream(FILE_PATH + fileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			String mimeType = URLConnection.guessContentTypeFromName(fileName);

			response.setContentType(mimeType);
			BufferedOutputStream bos = new BufferedOutputStream(
					response.getOutputStream());
			for (int data; (data = bis.read()) > -1;) {
				bos.write(data);
			}

			fis.close();
			bis.close();
			bos.close();
		} else {
			PrintWriter pw = response.getWriter();
			ObjectMapper mapper = new ObjectMapper();
			UploadRespVo result = new UploadRespVo();
			result.setMsg("request file [" + fileName + "] not found.");
			pw.write(mapper.writeValueAsString(result));
		}
	}
}
