package com.veviosys.vdigit.integrationCm.classes;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentExport {

    private List<Attrs> attrs;
    private String content;
    private String fileName;
    private boolean autoFolderEnable = true;
    private boolean isGenerated;
    private String name;
    private long type;
}
