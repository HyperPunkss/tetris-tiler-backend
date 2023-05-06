package com.hyperpunks.tetristilerbackend.web.services;

import com.hyperpunks.tetristilerbackend.library.Shape;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class A2TaskService {


    class ResponseItem{
        public String letter;
        public String content;

        public ResponseItem(String letter, String content) {
            this.letter = letter;
            this.content = content;
        }


    }

    public ResponseEntity<Object> getAllShapes(){
        List<ResponseItem> items = new ArrayList<>();
        for(Shape shape :  Shape.getAllShapes()){
            items.add(new ResponseItem(shape.getName(),shape.toGridString()));
        }
        return  ResponseEntity.ok(items);
    }

}
