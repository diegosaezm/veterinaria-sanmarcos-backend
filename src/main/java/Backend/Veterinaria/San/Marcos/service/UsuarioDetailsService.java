
package Backend.Veterinaria.San.Marcos.service;

import Backend.Veterinaria.San.Marcos.model.Usuario;
import Backend.Veterinaria.San.Marcos.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        // Buscar el usuario en MySQL
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Usuario no encontrado")
                );

        // Entregar sus datos a Spring Security
        return User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().name())
                .build();
    }
}
