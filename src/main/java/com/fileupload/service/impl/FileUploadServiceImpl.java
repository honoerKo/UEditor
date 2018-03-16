package com.fileupload.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fileupload.dao.FileUploadMapper;
import com.fileupload.model.FileUpload;
import com.fileupload.service.IFileUploadService;

@Service("fileUploadService")
public class FileUploadServiceImpl implements IFileUploadService {

	@Resource
	private FileUploadMapper fuMapper;
	
	public int save(FileUpload fu) {
		// TODO Auto-generated method stub
		try {
			this.fuMapper.insert(fu);
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	public FileUpload queryById(short s) {
		// TODO Auto-generated method stub
		return this.fuMapper.selectByPrimaryKey(s);
	}

	public List<FileUpload> queryAll() {
		// TODO Auto-generated method stub
		return this.fuMapper.selectByAll();
	}

	public FileUpload queryById(Short id) {
		// TODO Auto-generated method stub
		return this.fuMapper.selectByPrimaryKey(id);
	}

	public int deleteById(Short id) {
		// TODO Auto-generated method stub
		try {
			this.fuMapper.deleteByPrimaryKey(id);
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	public FileUpload queryByMaxId() {
		// TODO Auto-generated method stub
		return this.fuMapper.selectByMaxId();
	}

}
