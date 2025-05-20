package be.ucll.unit.repository;

import be.ucll.model.User;
import be.ucll.repository.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserRepositoryStub implements UserRepository {


    private  List<User> users = new ArrayList<>( List.of(
            new User("21Savage", 25, "21.savage@ucll.be", "john1234"),
            new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234"),
            new User("Jack Doe", 5, "jack.doe@ucll.be", "jack1234"),
            new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234"),
            new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234"))
    );


    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public <S extends User> S save(S entity) {
        users.add(entity);
        return entity;
    }

    @Override
    public void delete(User entity) {
        users.remove(entity);
    }
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {}

    @Override
    public User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean existsUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> findUsersByAgeBetween(int min, int max) {
        List<User> end = new ArrayList<>();
        for (User user : users){
            if (user.getAge() >= min && user.getAge() <= max ){
                end.add(user);
            }
        }
        return end;
    }

    @Override
    public List<User> findUserByName(String name) {
        return List.of();    }

    @Override
    public List<User> findUsersByName(String name) {
        List<User> end = new ArrayList<>();

        for (User user : users){
            if (user.getName().contains(name)){
                end.add(user);
            }
        }
        if (name.isEmpty()){
            end = users;
        }
        return end;
    }

    @Override public Optional<User> findById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public long count() {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteById(Long aLong) {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteAll() {
        users.clear();
    }

    @Override public <S extends User> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(users::add);
        return (List<S>) users;
    }

    @Override public List<User> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(users::remove);
    }

    @Override
    public List<User> findUsersByAgeAfter(int i) {
        return List.of();
    }


    @Override
    public void deleteUserByEmail(String email) {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<User> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public User getOne(Long aLong) {
        return null;
    }

    @Override
    public User getById(Long aLong) {
        return null;
    }

    @Override
    public User getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<User> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }
}
