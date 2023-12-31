package com.example.lab9.controller;


import com.example.lab9.entity.Historialpartidos;
import com.example.lab9.entity.Participante;
import com.example.lab9.entity.Partido;
import com.example.lab9.repository.EquipoRepository;
import com.example.lab9.repository.HistorialpartidosRepository;
import com.example.lab9.repository.ParticipanteRepository;
import com.example.lab9.repository.PartidoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/partido")
public class PartidoController {

    final PartidoRepository partidoRepository;
    final HistorialpartidosRepository historialPartidoRepository;

    final ParticipanteRepository participanteRepository;
    private final EquipoRepository equipoRepository;

    public PartidoController(PartidoRepository partidoRepository, HistorialpartidosRepository historialPartidoRepository, ParticipanteRepository participanteRepository,
                             EquipoRepository equipoRepository) {
        this.partidoRepository = partidoRepository;
        this.historialPartidoRepository = historialPartidoRepository;
        this.participanteRepository = participanteRepository;
        this.equipoRepository = equipoRepository;
    }


//    @GetMapping(value = {"/", ""})
//    public List<Partido> lista() {
//        return partidoRepository.findAll();
//    }

    //crear
    @PostMapping({"/registro","/registro/"})
    public ResponseEntity<HashMap<String, Object>> guardarPartido(
            @RequestBody Partido partido,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseJson = new HashMap<>();

        partidoRepository.save(partido);
        if (fetchId) {
            responseJson.put("id", partido.getId());
        }
        responseJson.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    // EJERCICIO 2B
    @GetMapping(value = "/getparticipantes")
    public ResponseEntity<?> buscarParticipante(@RequestParam(name = "idequipo", required = false) String idequipo) {
        if (idequipo != null) {
            HashMap<String, Object> respuesta2 = new HashMap<>();
            try {
                int id = Integer.parseInt(idequipo);
                Optional<Participante> byId = participanteRepository.findById(id);
                HashMap<String, Object> respuesta = new HashMap<>();

                if (byId.isPresent()) {
                    respuesta.put("result", "ok");
                    respuesta.put("participante", byId.get());
                } else {
                    respuesta.put("result", "no existe");
                }
                return ResponseEntity.ok(respuesta);
            } catch (NumberFormatException e) {
                respuesta2.put("result", "error");
                respuesta2.put("msg", "El ID ingresado es incorrecta");
                return ResponseEntity.badRequest().body(respuesta2);
            }
        } else {
            List<Participante> participante = listaParticipantes();
            return ResponseEntity.ok(participante);
        }
    }

    @GetMapping(value = {"/getparticipantesSegundo"})
    public List<Participante> listaParticipantes() {
        return participanteRepository.findAll();
    }


    // EJERCICIO 2C
    @GetMapping(value = "/gethistorialpartidos")
    public ResponseEntity<?> buscarPartido(@RequestParam(name = "idequipo", required = false) String idequipo) {
        if (idequipo != null) {
            HashMap<String, Object> respuesta2 = new HashMap<>();
            try {
                int id = Integer.parseInt(idequipo);
                Optional<Partido> byId = partidoRepository.findById(id);

                HashMap<String, Object> respuesta = new HashMap<>();

                if (byId.isPresent()) {
                    respuesta.put("result", "ok");
                    respuesta.put("producto", byId.get());
                } else {
                    respuesta.put("result", "no existe");
                }
                return ResponseEntity.ok(respuesta);
            } catch (NumberFormatException e) {
                respuesta2.put("result", "error");
                respuesta2.put("msg", "El ID ingresado es incorrecta");
                return ResponseEntity.badRequest().body(respuesta2);
            }
        } else {
            List<Historialpartidos> historialPartidos = listaPartidos();
            return ResponseEntity.ok(historialPartidos);
        }
    }

    @GetMapping(value = {"/gethistorialpartidosSegundo"})
    public List<Historialpartidos> listaPartidos() {
        return historialPartidoRepository.findAll();
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> gestionException(HttpServletRequest request) {
        HashMap<String, String> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar el contenido correctamente");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }

}