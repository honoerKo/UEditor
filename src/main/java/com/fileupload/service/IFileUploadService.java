package com.fileupload.service;

import java.util.List;

import com.fileupload.model.FileUpload;

public interface IFileUploadService {
	
	int save(FileUpload fu); 
	
	FileUpload queryByMaxId();
	
	List<FileUpload> queryAll();
	
	FileUpload queryById(Short id);
	
	int deleteById(Short id);
}
