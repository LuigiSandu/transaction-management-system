package project.transaction.management.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/transaction-management-system")
public class DummyController {


    @GetMapping
    public ResponseEntity<String> dummyPost() {
        return new ResponseEntity<String>("Hello World", OK);
    }

}
