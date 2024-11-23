package com.veviosys.vdigit.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.repositories.DocumentRepo;

@Service
public class ConvertService {

	@Autowired
	FileStorageServiceImpl fileStorageServiceImpl;
	@Autowired
	MasterConfigService masterConfigService;
    @Autowired
    DocumentRepo documentRepo;

    public HashMap<String, String> convertWordPdf(UUID id) throws IOException {
        Document doc = documentRepo.findById(id).orElse(null);
        String activePath = masterConfigService.findActivePath();
        String input = fileStorageServiceImpl.createTempConvertFile(id, activePath, doc);

        String output = activePath + "\\upload\\tempfiles\\" + id + ".pdf";
        try (

                InputStream docxInputStream = new FileInputStream(input);
                OutputStream pdfOutputStream = new FileOutputStream(output)) {
            IConverter converter = LocalConverter.builder().build();
            converter.convert(docxInputStream).as(DocumentType.MS_WORD)
                    .to(pdfOutputStream).as(DocumentType.PDF)
                    .execute();
            converter.shutDown();
            File file = new File(output);
            byte[] encoded = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
            HashMap<String, String> map = new HashMap<>();
            List<String> files = new ArrayList<>();
            map.put("pdf", new String(encoded, StandardCharsets.US_ASCII));
            map.put("filename", doc.getFileName());
            files.add(input);
            files.add(output);
            // fileStorageServiceImpl.deleteTempFiles(files);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
   
}
