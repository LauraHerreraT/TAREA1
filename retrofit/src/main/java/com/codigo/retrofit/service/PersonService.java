package com.codigo.retrofit.service;

import com.codigo.retrofit.aggregates.response.ReniecResponse;
import com.codigo.retrofit.entity.Persona;

import java.io.IOException;
import java.util.List;

public interface PersonService {

    ReniecResponse findByDni(String dni) throws IOException;

    List<Persona> getAll();
    Persona getById(Long id);
    Persona create(Persona persona);
    Persona update(Long id, Persona persona);
    boolean delete(Long id);
}
