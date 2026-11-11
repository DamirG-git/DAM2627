package com.da.dg2526.Ex10;

import com.da.dg2526.Utility;

import java.sql.SQLException;

public class U3Ex10 {

    public U3Ex10(int value) {
        if(value==1){
            EmployeeFunctionAdditions();
            VTInstituteFunctionAdditions();
        }else{
            artDBCreation();
        }

    }

    private void EmployeeFunctionAdditions() {
        var sqlEmployeeRecord = """
                DO $$ BEGIN
                    IF NOT EXISTS(
                        SELECT 1
                        FROM pg_type
                        WHERE typname = 'employee_record')
                
                    THEN
                    CREATE TYPE employee_record AS(
                        name VARCHAR(10),
                        job VARCHAR(9),
                        department VARCHAR(14)
                    );
                    END IF;
                END; $$
                """;

        var sqlEmployeeGetterFunc = """
                        CREATE OR REPLACE FUNCTION get_employee_data(p_employee_num INTEGER)
                            RETURNS employee_record
                
                        LANGUAGE SQL
                        AS $BODY$
                            SELECT e.ename, e.job, d.dname
                            FROM employee AS e JOIN public.dept d on d.deptno = e.deptno WHERE e.empno = p_employee_num;
                            $BODY$
                
                """;
        try (var conn = Utility.getConnectionEmployees();
             var psRecordGetterFunction = conn.prepareStatement(sqlEmployeeGetterFunc);
             var psEmployeeRecord = conn.prepareStatement(sqlEmployeeRecord);
             var psTestFunction = conn.prepareStatement("SELECT * FROM public.get_employee_data(?);")
        ) {

            System.out.println("Creating employee record...");
            psEmployeeRecord.execute();
            System.out.println("Creating employee getter function...");
            psRecordGetterFunction.execute();
            System.out.println("Testing the function...");
            psTestFunction.setInt(1, 103131);
            try (var rs = psTestFunction.executeQuery();) {
                while (rs.next()) {
                    System.out.printf("Test results: name [%s], job [%s], department [%s]\n", rs.getString("name"), rs.getString("job"), rs.getString("department"));
                }
            }


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private void VTInstituteFunctionAdditions() {
        var sqlCoursesWithSubjectsRecord = """
                DO $$ BEGIN
                    IF NOT EXISTS(
                            SELECT 1
                            FROM pg_type
                            WHERE typname = 'subject_record')
                    THEN
                    CREATE TYPE subject_record AS(
                        name VARCHAR(90),
                        subjects public.subjects[]
                    );
                    end if;
                end; $$
                """;

        var sqlGetSubjectsFromCourse = """
                CREATE OR REPLACE FUNCTION get_subjects_from_course(p_course_id INTEGER)
                    RETURNS subject_record
                LANGUAGE SQL
                AS $BODY$
                    Select ROW(c.name, array_agg(s ORDER BY s.year, s.name) )::public.subject_record
                    FROM courses c INNER JOIN public.subjects s on c.code = s.course
                    WHERE c.code = p_course_id group by c.code, c.name;
                $BODY$
                """;


        try (var conn = Utility.getConnectionVTInstitute();
             var psCreateRecord = conn.prepareStatement(sqlCoursesWithSubjectsRecord);
             var psCreateFunction = conn.prepareStatement(sqlGetSubjectsFromCourse);
             var psTestFunction = conn.prepareStatement("SELECT * FROM public.get_subjects_from_course(?);")) {


            System.out.println("Creating subjects record...");
            psCreateRecord.execute();
            System.out.println("Creating subjects getter function...");
            psCreateFunction.execute();
            System.out.println("Testing the function...");
            psTestFunction.setInt(1, 1);
            try (var rs = psTestFunction.executeQuery();) {
                while (rs.next()) {
                    System.out.printf("Test results: course_name [%s], course subjects [%s]\n",rs.getString("name"), rs.getString("subjects"));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private void artDBCreation(){

        var sqlCreateTypes = """
                DO $$ BEGIN
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'painting_enum')
                
                THEN
                    CREATE TYPE painting_enum as ENUM ('Oil', 'Watercolour', 'Pastel');
                end if;
                
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'material_enum')
                
                THEN
                    CREATE TYPE material_enum as ENUM ('Iron', 'Bronze', 'Marble');
                end if;
                
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'style_enum')
                
                THEN
                    CREATE TYPE style_enum as ENUM ('Greco Roman', 'Neo Classic', 'Cubism');
                end if;
                
                
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'pos_width' AND typtype = 'd')
                
                THEN
                    CREATE DOMAIN pos_width AS double precision check ( VALUE > 0 );
                end if;
                
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'pos_height' AND typtype = 'd')
                
                THEN
                    CREATE DOMAIN pos_height AS double precision check ( VALUE > 0 );
                end if;
                
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'pos_weight'  AND typtype = 'd')
                
                THEN
                    CREATE DOMAIN pos_weight AS double precision check ( VALUE > 0 );
                end if;
                
                IF NOT EXISTS(
                    SELECT 1
                    FROM pg_type
                    WHERE typname = 'dimensions_type')
                
                THEN
                    CREATE TYPE dimensions_type as(
                        width pos_width,
                        height pos_height
                    );
                end if;
                
                END;$$
                """;

        var sqlCreateTables = """
                DO $$ BEGIN
                    CREATE TABLE author (
                    name VARCHAR(100) not null,
                    nationality VARCHAR(100),
                    date_of_birth DATE not null,
                    code varchar GENERATED ALWAYS AS(
                        UPPER(LEFT(name,1) || SUBSTRING(name from position(' 'in name)+1 for 1)|| EXTRACT(YEAR FROM date_of_birth)::INTEGER::TEXT)
                    )STORED PRIMARY KEY
                );
                CREATE TABLE artwork (
                    code INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    title VARCHAR(100),
                    dated DATE,
                    author_code VARCHAR(100) references author(code)
                );
                
                CREATE TABLE sculpture (
                    material material_enum,
                    weight pos_weight
                ) INHERITS (artwork);
                
                
                
                CREATE TABLE painting (
                    type painting_enum,
                    dimensions dimensions_type
                ) INHERITS (artwork);
                
                
                END;
                $$
                """;

        try(var conn = Utility.getConnectionArt(); var psCreateTypes = conn.prepareStatement(sqlCreateTypes); var psCreateTables = conn.prepareStatement(sqlCreateTables)){
            System.out.println("Creating types...");
            psCreateTypes.execute();
            System.out.println("Creating tables...");
            psCreateTables.execute();
            System.out.println("Success");


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }




}
