package com.codigo.retrofit;

import com.codigo.retrofit.aggregates.ConexionDb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetrofitApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetrofitApplication.class, args);

		ConexionDb con1 = ConexionDb.obtenerInstancia();
		ConexionDb con2 = ConexionDb.obtenerInstancia();

		System.out.println(con1 == con2);
	}

}
