package com.test.db.model.ao;

/**
 * Created by VErmilov on 15.09.2017.
 */

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Table("IssueType")
@Preload
public interface IssueType extends Entity {

    String getGrantTask();
    void setGrantTask(String grantTask);
    
    String getGrantSubTask();
    void setGrantSubTask(String grantSubTask);
    
    String getRevokeTask();
    void setRevokeTask(String revokeTask);
    
    String getRevokeSubTask();
    void setRevokeSubTask(String revokeSubTask);
    
}
