package com.example.availabilityform

import androidx.room.*

@Dao
interface StudentDao {

    @Query("select * from student_table")
    fun getAll(): List<Student>

//    @Query("select * from student_table where roll_no like :roll limit 1")
//    suspend fun findByRoll(roll: Int): Student

    @Query("select * from student_table where id like :id limit 1")
    suspend fun findByRoll(id: Int): Student

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("delete from student_table")
    suspend fun deleteAll()

    @Query("delete from student_table where id like :id")
    suspend fun delete(id: Int)

    @Query("update student_table set first_name=:firstName, last_name=:lastName where id like :id")
    suspend fun update(firstName: String, lastName: String, id: Int)
}