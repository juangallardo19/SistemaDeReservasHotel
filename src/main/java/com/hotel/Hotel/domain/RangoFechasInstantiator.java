package com.hotel.Hotel.domain;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.EmbeddableInstantiator;
import org.hibernate.metamodel.spi.ValueAccess;

import java.time.LocalDateTime;

// Hibernate entrega los atributos del embeddable en orden alfabetico (fechaFin, fechaInicio),
// pero el constructor del record espera (fechaInicio, fechaFin). Sin este instanciador las
// fechas llegan invertidas y la validacion de RangoFechas falla al leer una Reserva.
public class RangoFechasInstantiator implements EmbeddableInstantiator {

    @Override
    public Object instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactory) {
        Object[] valores = valueAccess.getValues();
        LocalDateTime fechaFin = (LocalDateTime) valores[0];
        LocalDateTime fechaInicio = (LocalDateTime) valores[1];
        return new RangoFechas(fechaInicio, fechaFin);
    }

    @Override
    public boolean isInstance(Object object, SessionFactoryImplementor sessionFactory) {
        return object instanceof RangoFechas;
    }

    @Override
    public boolean isSameClass(Object object, SessionFactoryImplementor sessionFactory) {
        return object.getClass().equals(RangoFechas.class);
    }
}
