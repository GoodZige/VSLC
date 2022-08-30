package com.vslc.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IDataImportService {

    Map<String, Object> upload(List<MultipartFile> files);

    String add(String inspeDir, String hospitalID, Integer uploader);
}
