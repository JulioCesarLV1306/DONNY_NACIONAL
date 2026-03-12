package service;

import config.JPA;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import model.Usuario;

/**
 * Servicio de Gestión de Usuarios - V2.0
 * 
 * Gestiona operaciones sobre seg_usuario con nomenclatura refactorizada.
 * 
 * FUNCIONALIDADES:
 * - Búsqueda de usuarios por DNI
 * - Creación automática de usuarios con parsing de nombre completo
 * - Validación de duplicados
 * 
 * @author JC (Desarrollador)
 * @version 2.0
 * @since 2026-03-12
 */
public class UsuarioService extends JPA {

    /**
     * Busca un usuario por su DNI
     * 
     * @param dni DNI del usuario
     * @return Usuario encontrado o null
     */
    public Usuario findByDni(String dni) {
        Usuario usuario = null;
        try {
            TypedQuery<Usuario> query = getEntityManager()
                .createQuery("SELECT u FROM Usuario u WHERE u.cDni = :dni", Usuario.class);
            query.setParameter("dni", dni);
            usuario = query.getSingleResult();
        } catch (NoResultException e) {
            // No se encontró usuario
            usuario = null;
        } catch (Exception e) {
            System.out.println("Error al buscar usuario por DNI: " + e);
            e.printStackTrace();
        } finally {
            try {
                closeEntityManager();
            } catch (Exception e2) {
                System.out.println("Error al cerrar EntityManager: " + e2);
            }
        }
        return usuario;
    }

    /**
     * Crea un usuario si no existe en la base de datos.
     * Si existe, retorna el usuario existente.
     * 
     * @param dni DNI del usuario
     * @param nombreCompleto Nombre completo en formato "APELLIDO_PATERNO APELLIDO_MATERNO NOMBRES"
     * @return Usuario creado o existente con su ID asignado
     */
    public Usuario createIfNotExists(String dni, String nombreCompleto) {
        // Primero intenta buscar el usuario
        Usuario usuario = findByDni(dni);
        
        if (usuario != null) {
            System.out.println("[UsuarioService] Usuario ya existe con DNI: " + dni);
            return usuario;
        }
        
        // Si no existe, lo crea
        usuario = new Usuario();
        usuario.setCDni(dni);
        usuario.setNIdTipo(9); // Tipo "Invitado" por defecto (n_id_tipo = 9)
        usuario.setLActivo("S");
        
        // Parsear nombre completo en formato "APELLIDO_PAT APELLIDO_MAT NOMBRES"
        if (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) {
            String[] partes = nombreCompleto.trim().split("\\s+");
            if (partes.length >= 3) {
                usuario.setXApePaterno(partes[0]);
                usuario.setXApeMaterno(partes[1]);
                // Los demás son los nombres
                StringBuilder nombres = new StringBuilder();
                for (int i = 2; i < partes.length; i++) {
                    if (i > 2) nombres.append(" ");
                    nombres.append(partes[i]);
                }
                usuario.setXNombres(nombres.toString());
            } else if (partes.length == 2) {
                usuario.setXApePaterno(partes[0]);
                usuario.setXApeMaterno("");
                usuario.setXNombres(partes[1]);
            } else if (partes.length == 1) {
                usuario.setXApePaterno(partes[0]);
                usuario.setXApeMaterno("");
                usuario.setXNombres("");
            }
        } else {
            // Valores por defecto si no viene nombre
            usuario.setXApePaterno("USUARIO");
            usuario.setXApeMaterno("DESCONOCIDO");
            usuario.setXNombres("");
        }
        
        return create(usuario);
    }

    /**
     * Crea un nuevo usuario en la base de datos
     * 
     * @param usuario Objeto usuario con los datos a insertar
     * @return Usuario creado con ID asignado
     */
    public Usuario create(Usuario usuario) {
        EntityTransaction t = null;
        try {
            t = getEntityManager().getTransaction();
            if (!t.isActive()) {
                t.begin();
            }
            
            // Establece el usuario de auditoría
            usuario.setCAudUid(usuario.getCDni());
            
            getEntityManager().persist(usuario);
            t.commit();
            
            System.out.println("[UsuarioService] Usuario creado con ID: " + usuario.getNIdUsuario());
            
        } catch (Exception e) {
            System.out.println("Error al crear usuario: " + e);
            e.printStackTrace();
            if (t != null && t.isActive()) {
                try {
                    t.rollback();
                } catch (Exception ex) {
                    System.out.println("Error en rollback: " + ex);
                }
            }
        } finally {
            try {
                closeEntityManager();
            } catch (Exception e2) {
                System.out.println("Error al cerrar EntityManager: " + e2);
            }
        }
        return usuario;
    }
}
