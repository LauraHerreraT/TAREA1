package com.codigo.retrofit.controller;

import com.codigo.retrofit.aggregates.response.ReniecResponse;
import com.codigo.retrofit.entity.Persona;
import com.codigo.retrofit.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;


    @GetMapping("/find/{dni}")
    public ResponseEntity<ReniecResponse> findPerson(@PathVariable String dni) throws IOException {
        return new ResponseEntity<>(personService.findByDni(dni), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<Persona>> getAllPersons() {
        List<Persona> personas = personService.getAll();
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Persona> getPersonById(@PathVariable Long id) {
        Persona persona = personService.getById(id);
        if (persona != null) {
            return new ResponseEntity<>(persona, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping
    public ResponseEntity<Persona> createPerson(@RequestBody Persona persona) {
        Persona created = personService.create(persona);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Persona> updatePerson(@PathVariable Long id, @RequestBody Persona persona) {
        Persona updated = personService.update(id, persona);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        boolean deleted = personService.delete(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
