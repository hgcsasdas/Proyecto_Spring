package com.gestion.empleados.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gestion.empleados.entidades.Empleado;
import com.gestion.empleados.servicio.EmpleadoService;
import com.gestion.empleados.util.paginacion.PageRender;
import com.gestion.empleados.util.reportes.EmpleadoExporterPDF;

import com.lowagie.text.DocumentException;

import jakarta.validation.Valid;


@Controller 
public class EmpleadoController {
    
    @Autowired
    private EmpleadoService empleadoService;

	@GetMapping("/ver/{id}")
	public String verDetallesDelEmpleado(@PathVariable(value = "id") Long id,Map<String,Object> modelo,RedirectAttributes flash) {
		Empleado empleado = empleadoService.findOne(id);
		if(empleado == null) {
			flash.addFlashAttribute("error", "El empleado no existe en la base de datos");
			return "redirect:/listar";
		}
		
		modelo.put("empleado",empleado);
		modelo.put("titulo", "Detalles del empleado " + empleado.getNombre());
		return "ver";
	}
    //Se usa para listar
    @GetMapping({"/", "/listar", ""})
    public String listarEmpleados(@RequestParam(name = "page", defaultValue = "0")int page, Model modelo){
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Empleado> empleados = empleadoService.findAll(pageRequest);
        PageRender<Empleado> pageRender = new PageRender<Empleado>("/listar", empleados);

        modelo.addAttribute("titulo", "Listado de empleados");
        modelo.addAttribute("empleados", empleados);
        modelo.addAttribute("page", pageRender);

        return "listar";
    }   

    @GetMapping("/form")
    public String mostrarFormularioDeRegistrarEmpleado(Map<String, Object> modelo){
        Empleado empleado = new Empleado();
        modelo.put("empleado", empleado);
        modelo.put("titulo", "Registro de empleados");
        return "form";
    }

    @PostMapping("/form")
    public String guardarEmpleado(@Valid Empleado empleado, BindingResult result, Model modelo, RedirectAttributes flash){
        if (result.hasErrors()){
            modelo.addAttribute("titulo", "Registro de cliente");
            return "form";
        }

        String mensaje  = (empleado.getId() != null) ? "El empleado ha sido editado correctamente" : "El empleado ha sido registrado con éxito";
        empleadoService.save(empleado);

        flash.addFlashAttribute("success", mensaje);
        return "redirect:listar";
    }

    @GetMapping("/form/{id}")
    public String editarEmpleado(@PathVariable(value="id") Long id, Map<String, Object> modelo, RedirectAttributes flash){
        Empleado empleado = null;
        if (id > 0){
            empleado = empleadoService.findOne(id);
            if (empleado == null){
                flash.addFlashAttribute("error", "El id del empleado no existe en la base datos");
                return "redirect:/listar";
            }
        }  else{
            flash.addFlashAttribute("error", "El Id del empleado no puede ser 0");
            return "redirect:/listar";
        }

        modelo.put("empleado", empleado);
        modelo.put("titulo", "Editar empleados");

        return "form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEmpleado(@PathVariable(value="id") Long id, RedirectAttributes flash) {
        if (id > 0){
            empleadoService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito");
        }
        return "redirect:/listar";
    }

    @GetMapping("/exportarPDF")
	public void exportarListadoDeEmpleadosEnPDF(HttpServletResponse response) throws DocumentException, IOException {
		response.setContentType("application/pdf");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String fechaActual = dateFormatter.format(new Date());
		
		String cabecera = "Content-Disposition";
		String valor = "attachment; filename=Empleados_" + fechaActual + ".pdf";
		
		response.setHeader(cabecera, valor);
		
		List<Empleado> empleados = empleadoService.findAll();
		
        EmpleadoExporterPDF exporter = new EmpleadoExporterPDF(empleados);
		exporter.exportar(response);
	}
}