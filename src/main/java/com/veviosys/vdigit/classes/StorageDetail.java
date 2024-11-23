package com.veviosys.vdigit.classes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageDetail {
    

private String name;
private String path;
private long size;
private long freeSpace;
private int access;
}
