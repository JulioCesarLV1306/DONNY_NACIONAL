# GUÍA RÁPIDA: MIGRACIÓN FRONTEND ANGULAR

## 🚀 Cambios Principales

Esta guía muestra los cambios concretos aplicados al frontend Angular para adaptarlo al nuevo esquema de base de datos.

---

## 1. MODELO MODULO

### ❌ ANTES
```typescript
export interface Modulo {
    idModulo: number;
    pcIp: string;
    pcUsuario: string;
    pcClave: string;
    descripcion: string;
    ubicacion: string;
    estado: number;
}

// Uso en componente
let modulo = this.memoriaService.getModulo();
console.log(modulo.pcIp);           // ❌ Propiedad antigua
console.log(modulo.idModulo);        // ❌ Propiedad antigua
console.log(modulo.pcUsuario);       // ❌ Propiedad antigua
```

### ✅ DESPUÉS
```typescript
export interface Modulo {
    nIdModulo: number;      // n_ = numérico
    cPcIp: string;          // c_ = código
    cPcUsuario: string;     // c_ = código
    cPcClave: string;       // c_ = código
    xDescripcion: string;   // x_ = descripción
    cUbicacion: string;     // c_ = código
    nEstado: number;        // n_ = numérico
    
    // Auditoría
    fAud?: Date;            // f_ = fecha
    bAud?: string;          // b_ = bandera I/U/D
    cAudUid?: string;       // c_ = código UID
}

// Uso en componente
let modulo = this.memoriaService.getModulo();
console.log(modulo.cPcIp);          // ✅ Propiedad refactorizada
console.log(modulo.nIdModulo);      // ✅ Propiedad refactorizada
console.log(modulo.cPcUsuario);     // ✅ Propiedad refactorizada
```

---

## 2. MODELO ENCUESTA

### ❌ ANTES (Modelo Desnormalizado)
```typescript
export interface Encuesta {
    idModulo: number;
    dniSece: string;        // ❌ DNI desnormalizado
    nombreSece: string;     // ❌ Nombre desnormalizado
    calificacion: number;
}

// Crear encuesta en componente
let encuesta: Encuesta = {
    idModulo: modulo.idModulo,
    dniSece: persona.dni,           // ❌ Datos duplicados
    nombreSece: persona.nombre,     // ❌ Datos duplicados
    calificacion: 5
};
```

### ✅ DESPUÉS (Modelo Normalizado con FK)
```typescript
export interface Encuesta {
    // Campos nuevos
    nIdModulo: number;
    nIdUsuario: number;     // ✅ FK a seg_usuario (normalizado)
    nCalificacion: number;
    
    // Campos deprecados (temporal)
    /** @deprecated Usar nIdModulo */
    idModulo?: number;
    /** @deprecated Usar nIdUsuario */
    dniSece?: string;
    /** @deprecated Usar nIdUsuario */
    nombreSece?: string;
    /** @deprecated Usar nCalificacion */
    calificacion?: number;
}

// Crear encuesta en componente (VERSIÓN TEMPORAL)
let encuesta: Encuesta = {
    nIdModulo: modulo.nIdModulo,
    nIdUsuario: 0,  // TODO: Buscar usuario
    nCalificacion: 5,
    // Mantener campos deprecados por ahora
    idModulo: modulo.nIdModulo,
    dniSece: persona.dni,
    nombreSece: persona.nombre,
    calificacion: 5
};

// VERSIÓN FUTURA RECOMENDADA
const usuario = await this.usuarioService.crearSiNoExiste(
    persona.dni, 
    persona.apellidoPaterno, 
    persona.apellidoMaterno, 
    persona.nombres
).toPromise();

let encuesta: Encuesta = {
    nIdModulo: modulo.nIdModulo,
    nIdUsuario: usuario.nIdUsuario,  // ✅ FK real
    nCalificacion: 5
};
```

---

## 3. NUEVO MODELO USUARIO

### ✅ NUEVO ARCHIVO: usuario.ts
```typescript
export interface Usuario {
    nIdUsuario: number;
    nIdTipo: number;
    cDni: string;
    xApePaterno: string;
    xApeMaterno: string;
    xNombres: string;
    cTelefono?: string;
    xCorreo?: string;
    lActivo: string;        // "S" o "N"
    
    fAud?: Date;
    bAud?: string;
    cAudUid?: string;
}

export function getNombreCompleto(usuario: Usuario): string {
    return `${usuario.xApePaterno} ${usuario.xApeMaterno}, ${usuario.xNombres}`.trim();
}
```

---

## 4. SERVICIOS HTTP

### ❌ ANTES: downloader.service.ts
```typescript
descargar(expediente,nUnico,nIncidente,fechas,eleccion,tamanio,drive,modulo,persona){
  return this.http.put(`${this.url}/descarga?
    expediente=${expediente}&...
    &ipModulo=${modulo.pcIp}              // ❌
    &idModulo=${modulo.idModulo}          // ❌
    &usuarioModulo=${modulo.pcUsuario}    // ❌
    &dniPersona=${persona.dni}...`, drive);
}
```

### ✅ DESPUÉS: downloader.service.ts
```typescript
descargar(expediente,nUnico,nIncidente,fechas,eleccion,tamanio,drive,modulo,persona){
  // Usar propiedades refactorizadas de Modulo
  return this.http.put(`${this.url}/descarga?
    expediente=${expediente}&...
    &ipModulo=${modulo.cPcIp}             // ✅
    &idModulo=${modulo.nIdModulo}         // ✅
    &usuarioModulo=${modulo.cPcUsuario}   // ✅
    &dniPersona=${persona.dni}...`, drive);
}
```

### ✅ NUEVO: usuario.service.ts
```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario';
import { HOST_BACKEND } from '../shared/var.constant';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private url = `${HOST_BACKEND}/usuario`;

  constructor(private http: HttpClient) { }

  buscarPorDni(cDni: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.url}/buscar`, { params: { cDni } });
  }

  crearSiNoExiste(cDni: string, xApePaterno: string, 
                  xApeMaterno: string, xNombres: string): Observable<Usuario> {
    const usuario: Partial<Usuario> = {
      cDni, xApePaterno, xApeMaterno, xNombres,
      nIdTipo: 1,
      lActivo: 'S'
    };
    return this.http.post<Usuario>(`${this.url}/crear`, usuario);
  }

  listarActivos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.url}/listar`);
  }
}
```

---

## 5. COMPONENTES

### ❌ ANTES: dni-lectora.component.ts
```typescript
buscarPorDNI() {
  let modulo: Modulo = this.memoriaService.getModulo();
  if(this.dniIngresado.length >= 8){
    this.personasService.validarDNI(
      this.dniIngresado, 
      modulo.pcIp,        // ❌
      modulo.pcUsuario    // ❌
    ).subscribe(...);
  }
}
```

### ✅ DESPUÉS: dni-lectora.component.ts
```typescript
buscarPorDNI() {
  let modulo: Modulo = this.memoriaService.getModulo();
  if(this.dniIngresado.length >= 8){
    // Usar propiedades refactorizadas
    this.personasService.validarDNI(
      this.dniIngresado, 
      modulo.cPcIp,       // ✅
      modulo.cPcUsuario   // ✅
    ).subscribe(...);
  }
}
```

### ❌ ANTES: lista-expedientes.component.ts
```typescript
cargarListaExpedientes(page: any) {
  let persona: Persona = this.memoriaService.getPersona();
  let modulo: Modulo = this.memoriaService.getModulo();
  
  this.expedientesServices.buscarExpedientes(
    this.numero, this.anio, this.cuaderno, this.especialidad, 
    persona.dni, persona.tipo.idTipo, persona.nombre, 
    modulo.pcIp,        // ❌
    modulo.idModulo,    // ❌
    modulo.pcUsuario,   // ❌
    page, this.paginatorDefaultSize
  ).subscribe(...);
}
```

### ✅ DESPUÉS: lista-expedientes.component.ts
```typescript
cargarListaExpedientes(page: any) {
  let persona: Persona = this.memoriaService.getPersona();
  let modulo: Modulo = this.memoriaService.getModulo();
  
  // Usar propiedades refactorizadas
  this.expedientesServices.buscarExpedientes(
    this.numero, this.anio, this.cuaderno, this.especialidad, 
    persona.dni, persona.tipo.idTipo, persona.nombre, 
    modulo.cPcIp,       // ✅
    modulo.nIdModulo,   // ✅
    modulo.cPcUsuario,  // ✅
    page, this.paginatorDefaultSize
  ).subscribe(...);
}
```

### ❌ ANTES: encuesta.component.ts
```typescript
enviarEncuesta(valor:any){
  let modulo=this.memoriaService.getModulo();
  let persona=this.memoriaService.getPersona();

  let encuesta:Encuesta={
    idModulo:modulo.idModulo,      // ❌
    dniSece:persona.dni,            // ❌ Desnormalizado
    nombreSece:persona.nombre,      // ❌ Desnormalizado
    calificacion:valor              // ❌
  };
  
  this.encuestaService.crear(
    encuesta, 
    modulo.pcIp,       // ❌
    modulo.pcUsuario   // ❌
  ).subscribe(...);
}
```

### ✅ DESPUÉS: encuesta.component.ts (VERSIÓN TEMPORAL)
```typescript
enviarEncuesta(valor:any){
  let modulo=this.memoriaService.getModulo();
  let persona=this.memoriaService.getPersona();

  // Usar nueva estructura con compatibilidad temporal
  let encuesta:Encuesta={
    nIdModulo: modulo.nIdModulo,        // ✅
    nIdUsuario: 0,                      // TODO: Buscar usuario
    nCalificacion: valor,               // ✅
    // Campos deprecados (temporal)
    idModulo: modulo.nIdModulo,
    dniSece: persona.dni,
    nombreSece: persona.nombre,
    calificacion: valor
  };
  
  // Usar propiedades refactorizadas
  this.encuestaService.crear(
    encuesta, 
    modulo.cPcIp,       // ✅
    modulo.cPcUsuario   // ✅
  ).subscribe(...);
}
```

### ✅ VERSIÓN FUTURA RECOMENDADA: encuesta.component.ts
```typescript
// 1. Agregar UsuarioService al constructor
constructor(
  private encuestaService: EncuestaService,
  private usuarioService: UsuarioService,  // ✅ NUEVO
  private memoriaService: MemoriaService
) {}

// 2. Usar async/await para buscar usuario
async enviarEncuesta(valor:any){
  let modulo = this.memoriaService.getModulo();
  let persona = this.memoriaService.getPersona();

  try {
    // Buscar o crear usuario
    const usuario = await this.usuarioService.crearSiNoExiste(
      persona.dni,
      persona.apellidoPaterno,
      persona.apellidoMaterno,
      persona.nombres
    ).toPromise();

    // Crear encuesta con FK nIdUsuario
    let encuesta:Encuesta = {
      nIdModulo: modulo.nIdModulo,
      nIdUsuario: usuario.nIdUsuario,  // ✅ FK real
      nCalificacion: valor
    };
    
    this.encuestaService.crear(
      encuesta, 
      modulo.cPcIp, 
      modulo.cPcUsuario
    ).subscribe(
      data => this.mensajeService.goToDespedidaFinal(),
      error => this.mensajeService.goToDespedidaFinal()
    );
  } catch(error) {
    console.error('Error al enviar encuesta:', error);
    this.mensajeService.goToDespedidaFinal();
  }
}
```

---

## 6. TABLA DE REFERENCIA RÁPIDA

### Nomenclatura de Campos

| Prefijo | Tipo | Ejemplo Antiguo | Ejemplo Nuevo |
|---------|------|-----------------|---------------|
| `n_` | number | `idModulo` | `nIdModulo` |
| `c_` | string (código) | `pcIp` | `cPcIp` |
| `c_` | string (código) | `pcUsuario` | `cPcUsuario` |
| `x_` | string (texto) | `descripcion` | `xDescripcion` |
| `x_` | string (texto) | `nombreSece` | `xNombres` |
| `l_` | string (S/N) | - | `lActivo` |
| `f_` | Date | - | `fAud` |
| `b_` | string (I/U/D) | - | `bAud` |

### Modelos Refactorizados

| Modelo | Archivo | Estado | Cambios Principales |
|--------|---------|--------|---------------------|
| `Modulo` | modulo.ts | ✅ | 7 campos renombrados + 3 auditoría |
| `Encuesta` | encuesta.ts | ✅ | FK nIdUsuario + 4 campos deprecados |
| `Usuario` | usuario.ts | ✨ NUEVO | 12 campos completos |

### Servicios Refactorizados

| Servicio | Archivo | Métodos Actualizados |
|----------|---------|---------------------|
| `DownloaderService` | downloader.service.ts | 4 métodos |
| `ExpedientesService` | expedientes.service.ts | 2 métodos |
| `EncuestaService` | encuesta.service.ts | 1 método |
| `PersonasService` | personas.service.ts | 1 método |
| `UsuarioService` | usuario.service.ts | ✨ NUEVO (5 métodos) |

### Componentes Refactorizados

| Componente | Archivo | Líneas Modificadas |
|------------|---------|-------------------|
| `DniLectoraComponent` | dni-lectora.component.ts | 1 método |
| `EncuestaComponent` | encuesta.component.ts | 1 método |
| `ListaExpedientesComponent` | lista-expedientes.component.ts | 2 llamadas |
| `DescargaArchivosComponent` | descarga-archivos.component.ts | 2 métodos |

---

## 7. CHECKLIST DE MIGRACIÓN

### Para Desarrolladores que Agreguen Nuevos Componentes

- [ ] Al obtener Modulo, usar `modulo.cPcIp` en lugar de `modulo.pcIp`
- [ ] Al obtener Modulo, usar `modulo.nIdModulo` en lugar de `modulo.idModulo`
- [ ] Al obtener Modulo, usar `modulo.cPcUsuario` en lugar de `modulo.pcUsuario`
- [ ] Al crear Encuesta, usar `nIdModulo`, `nIdUsuario`, `nCalificacion`
- [ ] Antes de crear Encuesta, buscar o crear Usuario con `usuarioService.crearSiNoExiste()`
- [ ] NO usar campos deprecados en código nuevo (`dniSece`, `nombreSece`, etc.)
- [ ] Verificar que servicios HTTP pasen parámetros con nombres correctos
- [ ] Ejecutar `ng build` para verificar errores TypeScript

---

## 8. ERRORES COMUNES Y SOLUCIONES

### Error: "Property 'pcIp' does not exist on type 'Modulo'"
```typescript
// ❌ ERROR
let ip = modulo.pcIp;

// ✅ SOLUCIÓN
let ip = modulo.cPcIp;
```

### Error: "Property 'idModulo' does not exist on type 'Modulo'"
```typescript
// ❌ ERROR
let id = modulo.idModulo;

// ✅ SOLUCIÓN
let id = modulo.nIdModulo;
```

### Error: "Encuesta no tiene nIdUsuario en base de datos (NULL)"
```typescript
// ❌ PROBLEMA: No se busca usuario antes de crear encuesta
let encuesta: Encuesta = {
    nIdModulo: modulo.nIdModulo,
    nIdUsuario: 0,  // ❌ Placeholder incorrecto
    nCalificacion: 5
};

// ✅ SOLUCIÓN: Buscar o crear usuario
const usuario = await this.usuarioService.crearSiNoExiste(
    persona.dni, apellidoP, apellidoM, nombres
).toPromise();

let encuesta: Encuesta = {
    nIdModulo: modulo.nIdModulo,
    nIdUsuario: usuario.nIdUsuario,  // ✅ FK real
    nCalificacion: 5
};
```

### Error: "Backend returns 400 Bad Request"
```typescript
// ❌ PROBLEMA: Parámetros HTTP incorrectos
this.http.get(`${url}/buscar`, { 
    params: { cPcIp: modulo.cPcIp }  // ❌ Backend espera 'ipModulo'
});

// ✅ SOLUCIÓN: Usar nombres de parámetros esperados por backend
this.http.get(`${url}/buscar`, { 
    params: { ipModulo: modulo.cPcIp }  // ✅ Nombre correcto
});
```

---

## 9. COMANDOS ÚTILES

```bash
# Instalar dependencias
cd asistente-expedientes-angular-main_FRONTEND/asistente-expedientes-angular-main
npm install

# Verificar errores TypeScript
npx tsc --noEmit

# Compilar proyecto
ng build

# Ejecutar en desarrollo
ng serve

# Build de producción
ng build --prod

# Ver estructura de módulos
npm ls @angular/core
```

---

## 10. PRÓXIMOS PASOS

1. **Implementar búsqueda de usuario en encuesta.component.ts**
   - Reemplazar `nIdUsuario: 0` por lookup real
   - Estimar 30 minutos

2. **Ejecutar pruebas de integración**
   - Validar flujo completo con backend
   - Estimar 2 horas

3. **Eliminar campos @deprecated**
   - Una vez confirmado que todo funciona
   - Estimar 30 minutos

4. **Refactorizar otros modelos**
   - Aplicar misma estrategia a Expediente, Fecha, etc.
   - Estimar 1 hora por modelo

---

## 📞 CONTACTO

¿Dudas o problemas con la migración?
- Ver documentación completa: `REFACTORIZACION_FRONTEND_ANGULAR.md`
- Ver backend refactorizado: `REFACTORIZACION_COMPLETA_SPRING.md`
- Ver desktop client: `REFACTORIZACION_COMPLETA_DONYDRIVECLIENT.md`

---

*Guía generada: Diciembre 2024*
