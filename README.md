# callcenter
Este proyecto implementa un Dispatcher en java para representar la atención de las llamabas de los clientes de un callcenter.

Las responsabilidades del Dispatcher son: 

- Asignar a un CallHandler las llamadas que recibe, teniendo en cuenta la prioridad del CallHanderler para recibir llamadas, se deben asignar al CallHanderler disponible con mayor prioridad.

- Gestionar las llamadas que no pueden ser atendidas de manera inmediata.

Un CallHandler implementa la interface CallHandler que tiene tres métodos:

- boolean atenderLlamada(Llamada llamada);
- int getPrioridad();
- boolean isDisponible();

La aplicación provee una implementación de CallHandler en la clase abstracta Empleado, que a su vez es extendida por las clases Operador, Supervisor y Director, cada una con una prioridad diferente.

El Dispatcher permite manejar múltiples llamadas de manera concurrente sin importar el numero de CallHandlers disponibles, para lograr esto delega a un CallWatingHandler el manejo durante el tiempo de espera de las llamadas que no pueden ser atenidas de manera inmediata.

En el proyecto también se implementa la interface CallWatingHandler en la clase MemoryCallWaitingHandler, que se encarga de enviar un mensaje de espera a las llamadas que están en la cola de espera. la interfacer CallWatingHandler tiene los siguientes metodos.

- void add(Llamada llamada);
- Llamada poll();
- boolean isEmpty();
