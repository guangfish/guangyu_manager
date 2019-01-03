/**
  * Copyright 2018 bejson.com 
  */
package com.bt.om.pdd.pid.vo;
import java.util.List;

/**
 * Auto-generated: 2018-12-05 11:0:42
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class P_id_query_response {

    private List<P_id_list> p_id_list;
    private int total_count;
    public void setP_id_list(List<P_id_list> p_id_list) {
         this.p_id_list = p_id_list;
     }
     public List<P_id_list> getP_id_list() {
         return p_id_list;
     }

    public void setTotal_count(int total_count) {
         this.total_count = total_count;
     }
     public int getTotal_count() {
         return total_count;
     }

}