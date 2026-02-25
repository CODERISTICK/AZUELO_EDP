/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick;

import java.sql.Timestamp;

public class Notification {
    public int id;
    public Integer senderUserId;
    public Integer receiverUserId;
    public String receiverRole;

    public String title;
    public String message;

    public String type;      // INFO/ALERT/REQUEST/SYSTEM
    public String priority;  // NORMAL/IMPORTANT
    public String status;    // UNREAD/READ/...

    public Timestamp createdAt;

    public boolean isUnread() {
        return status != null && status.equalsIgnoreCase("UNREAD");
    }
}
