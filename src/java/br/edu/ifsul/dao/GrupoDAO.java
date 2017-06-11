
package br.edu.ifsul.dao;

import br.edu.ifsul.jpa.EntityManagerUtil;
import br.edu.ifsul.modelo.Grupo;
import br.edu.ifsul.modelo.Produto;
import br.edu.ifsul.util.Util;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;


 
public class GrupoDAO<T> extends DAOGenerico<Grupo> implements Serializable {
    
    public GrupoDAO(){
        super();
        super.setClassePersistence(Grupo.class);
        super.setOrdem("nome");
    }
 
}
