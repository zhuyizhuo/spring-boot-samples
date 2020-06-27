package com.github.zhuyizhuo.ldap.sample.repository;

import com.github.zhuyizhuo.ldap.sample.entity.Person;
import org.springframework.data.repository.CrudRepository;

import javax.naming.Name;

public interface PersonRepository extends CrudRepository<Person, Name> {
}
