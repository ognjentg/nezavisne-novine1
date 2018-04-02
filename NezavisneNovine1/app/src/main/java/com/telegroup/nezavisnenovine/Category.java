package com.telegroup.nezavisnenovine;

/**
 * Created by ZiB on 27.3.2018..
 */

public class Category{
   private String name;
    private String menuID;
    private  String color;


        public Category(String name, String menuID, String color) {
            this.name = name;
            this.menuID = menuID;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMenuID() {
            return menuID;
        }

        public void setMenuID(String menuID) {
            this.menuID = menuID;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
}
