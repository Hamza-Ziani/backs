package com.veviosys.vdigit.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.veviosys.vdigit.classes.File64;
import com.veviosys.vdigit.classes.cryptedFile;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.repositories.DocumentRepo;
import com.veviosys.vdigit.repositories.FolderRepo;
import com.veviosys.vdigit.repositories.MasterConfigRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class LedgerService {

  @Autowired
  FileStorageServiceImpl fileStorageServiceImpl;
  @Autowired
  DocumentRepo documentRepo;
  @Autowired
  FolderRepo folderRepo;
  @Autowired
  AlimentationService alimentationService;
  @Autowired
  MasterConfigRepository configRepository;

  // DECLARE COURRIER IN LEDGER
  public void setupAndSave(UUID folderId,String secondary) throws IOException {
    Folder folder = folderRepo.getOne(folderId);
    List<cryptedFile> cryptedFiles = new ArrayList<cryptedFile>();
    for (DocumentFolder df : folder.getDocuments()) {
      if (Objects.nonNull(df.getDocument().getContentType())) {
        File64 f = fileStorageServiceImpl.loadBase64String(df.getDocument().getId(),secondary);
        String infoC = getEntries(df.getDocument());
        String hash = hashFile(f.getFileData());
        cryptedFiles.add(new cryptedFile(hash, infoC));
        System.out.println(hash);
      }
    }

    SaveInLedger(cryptedFiles);

  }

  // HASH FILE TO SHA256
  String hashFile(String data) {
    String hash = "";

    hash = Hashing.sha256().hashString(data.replace("\n", "").replace("\r", ""), StandardCharsets.UTF_8).toString();

    return hash;
  }

  // CRYPTE DOCS INFO
  String getEntries(Document d) {
    HashMap<String, String> data = new HashMap<>();
    Gson o = new Gson();
    for (DocumentAttributeValue dav : d.getAttributeValues()) {
      if(!dav.getAttribute().getName().equals("Fichier"))
      data.put(dav.getAttribute().getLabelfr(), dav.getValue().getValue());
    }

    String _infos = o.toJson(data), asciisString = "";
    for (int i = 0; i < _infos.length(); i++) {
      asciisString += (int) _infos.charAt(i) + 5;
      if (i != _infos.length() - 1)
        asciisString += ',';
    }

    return Base64.getEncoder().encodeToString(asciisString.getBytes(StandardCharsets.UTF_8));
  }

  @Value("${documania.ledger.url}")
  private String URL;
  @Value("${documania.ledger.password}")
  private String password;
  @Value("${documania.ledger.username}")
  private String username;

  // SEND RESULT BY A WEB SERVICE TO SAVE IN LEDGER
  public void SaveInLedger(List<cryptedFile> list) {
    User master = alimentationService.getMaster();
    String url = configRepository.findByMasterUserIdAndConfigName(master.getUserId(), "LEDGER").getConfigValue();
    System.out.println(url.split("|")[0] + "--------" + url.split("\\|")[1]);
    WebClient.create().post().uri(url.split("\\|")[1]).accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
        .body(BodyInserters.fromValue(new Gson().toJson(list))).retrieve().bodyToMono(JsonNode.class).block();
System.out.println("sent done");
  }
}
