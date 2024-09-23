package cr.ac.una.springbootaopmaven.repository;

import cr.ac.una.springbootaopmaven.entity.Persona;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Repository
@Profile("in-memory")
public class InMemoryPersonaRepository implements PersonaRepository {

    private Map<Long, Persona> personas = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong();

    @Override
    public <S extends Persona> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Persona> findAll() {
        return new ArrayList<>(personas.values());
    }

    @Override
    public List<Persona> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Optional<Persona> findById(Long id) {
        return Optional.ofNullable(personas.get(id));
    }

    @Override
    public Persona save(Persona persona) {
        if (persona.getId() == null) {
            persona.setId(idGenerator.incrementAndGet());
        }
        personas.put(persona.getId(), persona);
        return persona;
    }

    @Override
    public void deleteById(Long id) {
        personas.remove(id);
    }

    @Override
    public void delete(Persona entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Persona> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public boolean existsById(Long id) {
        return personas.containsKey(id);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Persona> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Persona> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Persona> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Persona getOne(Long aLong) {
        return null;
    }

    @Override
    public Persona getById(Long aLong) {
        return null;
    }

    @Override
    public Persona getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Persona> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Persona> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Persona> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Persona> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Persona> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Persona> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Persona, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Persona> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Persona> findAll(Pageable pageable) {
        return null;
    }
}
