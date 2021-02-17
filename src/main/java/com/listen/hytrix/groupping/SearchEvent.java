package com.listen.hytrix.groupping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchEvent {

  private String searchId;

  private String word;

}
