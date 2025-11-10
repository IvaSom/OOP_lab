package ru.ssau.tk.swc.labs.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceProvider {
    Connection getConnection() throws SQLException;
}
