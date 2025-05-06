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

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final ClientReniecService apiClient =
            ClientRetrofit.getRetrofit().create(ClientReniecService.class);

    @Value("${token.api}")
    private String accessToken;

    @Override
    public ReniecResponse findByDni(String dni) throws IOException {
        Call<ReniecResponse> call = buildRequest(dni);
        Response<ReniecResponse> response = call.execute();
        return (response.isSuccessful() && response.body() != null) ? response.body() : new ReniecResponse();
    }

    private Call<ReniecResponse> buildRequest(String dni) {
        return apiClient.findReniec("Bearer " + accessToken, dni);
    }

    @Override
    public List<Persona> getAll() {
        List<Persona> resultList = new ArrayList<>();
        String query = "SELECT * FROM personas";

        try (
                Connection connection = ConexionDb.obtenerInstancia().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                Persona p = new Persona();
                p.setId(resultSet.getInt("id"));
                p.setNombres(resultSet.getString("nombres"));
                p.setApellidoPaterno(resultSet.getString("apellido_paterno"));
                p.setApellidoMaterno(resultSet.getString("apellido_materno"));
                p.setDni(resultSet.getString("dni"));
                resultList.add(p);
            }
        } catch (SQLException ex) {
            logger.error("Error recuperando lista de personas", ex);
        }

        return resultList;
    }

    @Override
    public Persona getById(Long id) {
        String query = "SELECT * FROM personas WHERE id = ?";
        Persona persona = null;

        try (
                Connection connection = ConexionDb.obtenerInstancia().getConnection();
                PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    persona = new Persona();
                    persona.setId(rs.getInt("id"));
                    persona.setNombres(rs.getString("nombres"));
                    persona.setApellidoPaterno(rs.getString("apellido_paterno"));
                    persona.setApellidoMaterno(rs.getString("apellido_materno"));
                    persona.setDni(rs.getString("dni"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error al buscar persona por ID", e);
        }

        return persona;
    }

    @Override
    public Persona create(Persona persona) {
        String insertSQL = "INSERT INTO personas (nombres, apellido_paterno, apellido_materno, dni) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = ConexionDb.obtenerInstancia().getConnection();
                PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, persona.getNombres());
            ps.setString(2, persona.getApellidoPaterno());
            ps.setString(3, persona.getApellidoMaterno());
            ps.setString(4, persona.getDni());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        persona.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error insertando nueva persona", e);
        }

        return persona;
    }

    @Override
    public Persona update(Long id, Persona persona) {
        String updateSQL = "UPDATE personas SET nombres = ?, apellido_paterno = ?, apellido_materno = ?, dni = ? WHERE id = ?";

        try (
                Connection conn = ConexionDb.obtenerInstancia().getConnection();
                PreparedStatement ps = conn.prepareStatement(updateSQL)
        ) {
            ps.setString(1, persona.getNombres());
            ps.setString(2, persona.getApellidoPaterno());
            ps.setString(3, persona.getApellidoMaterno());
            ps.setString(4, persona.getDni());
            ps.setLong(5, id);

            if (ps.executeUpdate() > 0) {
                persona.setId(id.intValue());
                return persona;
            }

        } catch (SQLException ex) {
            logger.error("Error actualizando persona", ex);
        }

        return null;
    }

    @Override
    public boolean delete(Long id) {
        String deleteSQL = "DELETE FROM personas WHERE id = ?";

        try (
                Connection conn = ConexionDb.obtenerInstancia().getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteSQL)
        ) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error eliminando persona", e);
        }

        return false;
    }
}
