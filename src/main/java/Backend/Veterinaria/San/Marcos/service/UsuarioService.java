
package Backend.Veterinaria.San.Marcos.service;

import Backend.Veterinaria.San.Marcos.model.Rol;
import Backend.Veterinaria.San.Marcos.model.Usuario;
import Backend.Veterinaria.San.Marcos.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar un nuevo usuario con rol CLIENTE
    public Usuario registrarCliente(
            String correo,
            String contrasena) {

        // 1. Validar el correo
        if (correo == null || correo.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El correo es obligatorio"
            );
        }

        String correoNormalizado =
                correo.trim().toLowerCase(Locale.ROOT);

        if (correoNormalizado.length() > 150 ||
            !correoNormalizado.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
            )) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Correo invalido"
            );
        }

        // 2. Comprobar si el correo ya existe
        if (usuarioRepository.findByCorreo(
                correoNormalizado).isPresent()) {

            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El correo ya esta registrado"
            );
        }

        // 3. Validar la contraseña
        if (contrasena == null ||
            contrasena.length() < 8 ||
            contrasena.getBytes(
                StandardCharsets.UTF_8).length > 72) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La contrasena debe tener al menos 8 caracteres y no superar 72 bytes"
            );
        }

        // 4. Crear el usuario
        Usuario usuario = new Usuario();

        usuario.setCorreo(correoNormalizado);

        // Guardar el HASH, nunca la contraseña original
        usuario.setPasswordHash(
            passwordEncoder.encode(contrasena)
        );

        // El registro publico siempre crea CLIENTES
        usuario.setRol(Rol.CLIENTE);

        // 5. Guardar en MySQL
        return usuarioRepository.save(usuario);
    }
}
