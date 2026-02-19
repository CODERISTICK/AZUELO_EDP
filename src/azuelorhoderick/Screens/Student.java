/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package azuelorhoderick.Screens;

/**
 *
 * @author Azuelo-Rh
 */
public class Student {
    
    private String name;
    private int age;
    
    public void setName(String name){
        this.name = name;
    }
    
    
    public void setAge(int age){
        this.age = age;
    }
    
    
    public void displayDetails(){
        System.out.println("My name is " + name + " and my age is " + age);
    }
}
