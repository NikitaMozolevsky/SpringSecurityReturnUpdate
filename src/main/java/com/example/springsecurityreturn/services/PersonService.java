package com.example.springsecurityreturn.services;

import com.example.springsecurityreturn.entity.Person;
import com.example.springsecurityreturn.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public boolean personIsExist(String username) {
        return peopleRepository.findByUsername(username).isPresent();
    }

    @Transactional //the changes in DB is executing
    public void register(Person person) {
        peopleRepository.save(person);
    }
}
