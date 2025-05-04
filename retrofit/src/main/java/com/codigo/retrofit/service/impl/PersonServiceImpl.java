package com.codigo.retrofit.service.impl;

import com.codigo.retrofit.aggregates.response.ReniecResponse;
import com.codigo.retrofit.entity.Persona;
import com.codigo.retrofit.retrofit.ClientReniecService;
import com.codigo.retrofit.retrofit.ClientRetrofit;
import com.codigo.retrofit.service.PersonService;
import com.codigo.retrofit.aggregates.ConexionDb;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

    ClientReniecService retrofitPreConfig =
            ClientRetrofit.getRetrofit()
                    .create(ClientReniecService.class);

    @Value("${token.api}")
    private String token;

    @Override
    public ReniecResponse findByDni(String dni) throws IOException {
        Response<ReniecResponse> executeReniec = preparedClient(dni).execute();
        if (executeReniec.isSuccessful() && Objects.nonNull(executeReniec.body())) {
            return executeReniec.body();
        }
        return new ReniecResponse();
    }

    private Call<ReniecResponse> preparedClient(String dni) {
        return retrofitPreConfig.findReniec("Bearer " + token, dni);
    }


    @Override
    public List<Persona> getAll() {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas";

        try (Connection conn = ConexionDb.obtenerInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Persona persona = new Persona();
                persona.setId(rs.getInt("id"));
                persona.setNombres(rs.getString("nombres"));
                persona.setApellidoPaterno(rs.getString("apellido_paterno"));
                persona.setApellidoMaterno(rs.getString("apellido_materno"));
                persona.setDni(rs.getString("dni"));
                personas.add(persona);
            }

        } catch (SQLException e) {
            log.error("Error al obtener personas: ", e);
        }

        return personas;
    }

    @Override
    public Persona getById(Long id) {
        String sql = "SELECT * FROM personas WHERE id = ?";
        Persona persona = null;

        try (Connection conn = ConexionDb.obtenerInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                persona = new Persona();
                persona.setId(rs.getInt("id"));
                persona.setNombres(rs.getString("nombres"));
                persona.setApellidoPaterno(rs.getString("apellido_paterno"));
                persona.setApellidoMaterno(rs.getString("apellido_materno"));
                persona.setDni(rs.getString("dni"));
            }

        } catch (SQLException e) {
            log.error("Error al obtener persona por ID: ", e);
        }

        return persona;
    }

    @Override
    public Persona create(Persona persona) {
        String sql = "INSERT INTO personas (nombres, apellido_paterno, apellido_materno, " +
                "dni) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConexionDb.obtenerInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, persona.getNombres());
            stmt.setString(2, persona.getApellidoPaterno());
            stmt.setString(3, persona.getApellidoMaterno());
            stmt.setString(4, persona.getDni());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                persona.setId(rs.getInt("id"));
            }

        } catch (SQLException e) {
            log.error("Error al crear persona: ", e);
        }

        return persona;
    }

    @Override
    public Persona update(Long id, Persona persona) {
        String sql = "UPDATE personas SET nombres = ?, apellido_paterno = ?, apellido_materno = ?," +
                " dni = ? WHERE id = ?";

        try (Connection conn = ConexionDb.obtenerInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, persona.getNombres());
            stmt.setString(2, persona.getApellidoPaterno());
            stmt.setString(3, persona.getApellidoMaterno());
            stmt.setString(4, persona.getDni());
            stmt.setLong(5, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                persona.setId(id.intValue());
                return persona;
            }

        } catch (SQLException e) {
            log.error("Error al actualizar persona: ", e);
        }

        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM personas WHERE id = ?";

        try (Connection conn = ConexionDb.obtenerInstancia().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            log.error("Error al eliminar persona: ", e);
        }

        return false;
    }
}
