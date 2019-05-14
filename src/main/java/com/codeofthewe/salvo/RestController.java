/*package com.codeofthewe.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class RestController {
    @Autowired
    private ContactRepository repo;

    @RequestMapping("/contacts")
    public List<Contact> getAll() {
        return repo.findAll();
    }
}
*/