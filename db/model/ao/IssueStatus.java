package com.test.db.model.ao;

/**
 * Created by VErmilov on 15.09.2017.
 */

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("IssueType")
@Preload
public interface IssueStatus extends Entity {

    String getName();
    void setName(String name);

    String getVal();
    void setVal(String val);
    
}
