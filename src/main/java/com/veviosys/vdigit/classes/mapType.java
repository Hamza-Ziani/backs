package com.veviosys.vdigit.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class mapType {
  public  String key;
  public  String libelle;
  public int value;
  public int rep;
  public int visible;
}
