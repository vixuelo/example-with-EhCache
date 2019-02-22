import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class Main {
	Connection cnx;
	provincias pro;
	CacheManager cm = CacheManager.newInstance();
	Cache cache = cm.getCache("cache");

	public static void main(String[] args) {
		Main main = new Main();
		main.conectar();
		float t0=System.currentTimeMillis();
		main.recorrer(main.consulta());
		System.out.println("ha tardado "+ String.valueOf(System.currentTimeMillis()-t0)+" milisegundos en acceder a la base de datos");
		main.setcache(main.consulta());
		main.getcache(100);
		main.desconectar();

	}

	public void conectar() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			cnx = DriverManager.getConnection("jdbc:mysql://192.168.34.5:3306/jcubero", "jcubero", "jcubero");

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public void desconectar() {
		try {
			cnx.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	@SuppressWarnings("finally")
	public ArrayList consulta() {
//		obtejos necesarios
		Statement stat;
		ArrayList<Object> resultados = new ArrayList();
		provincias pro;
//		variables necesarias
		int id;
		String nombre;
		try {

			Statement sentencia = cnx.createStatement();
			String sql = ("select * from provincias;").toString();
			ResultSet res = sentencia.executeQuery(sql);
			resultados.clear();
			while (res.next()) {
				id = res.getInt(1);
				nombre = res.getString(2);

				pro = new provincias(id, nombre);
				resultados.add(pro);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return resultados;
		}

	}

	public void recorrer(ArrayList resultados) {

		System.out.println(resultados);
		provincias pro;
		Iterator it;
		it = resultados.iterator();
		System.out.println("Resultado de la select: ");
		while (it.hasNext()) {

			pro = (provincias) it.next();

			System.out.println("id: " + pro.getId());
			System.out.println("nombre: " + pro.getNombre());
		

		}
	}
	public void setcache(ArrayList resultados) {
		
		System.out.println(resultados);
		provincias pro;
		Iterator it;
		it = resultados.iterator();
		System.out.println("Resultado de la select: ");
		int i =1;
		while (it.hasNext()) {

			pro = (provincias) it.next();
			cache.put(new Element("id"+i,pro.getId()));
			cache.put(new Element("nombre"+i,pro.getNombre()));
			i=+1;

		}
		
	}
	public void getcache(int numero) {
		float t0= System.currentTimeMillis();
		for (int i=1;i<numero;i++) {
			Element ele = cache.get("id"+i);
			String output = (ele.getObjectValue().toString());
			System.out.println(output);
			ele = cache.get("nombre"+i);
			output = (ele.getObjectValue().toString());
			System.out.println(output);
			
		}
		float dt=System.currentTimeMillis()-t0;
		System.out.println("ha tardado "+dt+" milisegundos en acceder a los datos de la cache");
	}

}
