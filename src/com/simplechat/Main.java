package com.simplechat;

import src.com.simplechat.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.getConnection();
    }
}
