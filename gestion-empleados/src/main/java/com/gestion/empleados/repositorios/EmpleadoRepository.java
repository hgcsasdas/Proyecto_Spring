package com.gestion.empleados.repositorios;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gestion.empleados.entidades.Empleado;

public interface EmpleadoRepository extends PagingAndSortingRepository<Empleado, Long>{

    List<Empleado> findAll();

    Empleado findById(Long id);

    void deleteById(Long id);

    void save(Empleado empleado);
    
}
