package com.opview.summary.entity;

import java.util.List;

public class SummaryApiResponse {

 private ResponseInfo response_info;
 private List<Article> result;

 // 您需要為這兩個欄位添加 Getter 和 Setter 方法
 // 通常 IDE 都可以自動生成
 public ResponseInfo getResponseInfo() {
     return response_info;
 }

 public void setResponseInfo(ResponseInfo response_info) {
     this.response_info = response_info;
 }

 public List<Article> getResult() {
     return result;
 }

 public void setResult(List<Article> result) {
     this.result = result;
 }
}
