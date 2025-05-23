package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DataChange<T> {

    private ChangeType changeType;

    private T data;

    public enum ChangeType {
        INSERT, UPDATE, DELETE
    }
}
