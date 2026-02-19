/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick.Screens;

/**
 *
 * @author Azuelo-Rh
 */
public class User {
    private String username;
    private String password;
    
    public void setUserAndPass(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public void loginUser(){
        System.out.println("The username is " + username + " and the password is " + password);
    }
}
