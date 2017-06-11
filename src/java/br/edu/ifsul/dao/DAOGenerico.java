/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsul.dao;
import br.edu.ifsul.jpa.EntityManagerUtil;
import br.edu.ifsul.util.Util;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
/**
 *
 * @author Renan
 */
public class DAOGenerico<T> {
    private List<T> listaObjetos;
    protected EntityManager em;
    protected Class classePersistence;
    private String mensagem = "";
    protected String ordem = "id";
    protected String filtro = "";
    protected Integer maximoObjetos = 2;
    protected Integer posicaoAtual=0;
    protected Integer totalObjetos = 0;
    private List<T>listaTodos;
    Pattern pt;
    
    
    
    public DAOGenerico(){
        em = EntityManagerUtil.getEntityManager();
    }

    public List<T> getListaObjetos() {
        String jpql = "from "+classePersistence.getSimpleName();
        String where = "";
        
        if (filtro.length()>0) {
            Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
            Matcher match= pt.matcher(filtro);
            while(match.find())
            {
                String s= match.group();
            filtro=filtro.replaceAll("\\"+s, "");
            }
        }
        
        
        if (filtro.length() > 0) {
            if (ordem.equals("id")) {
                try{               
                    Integer.parseInt(filtro);
                    where += " where " + ordem + " = '" + filtro + "' ";
                }catch(Exception e){
                }
            }else{
                where += " where upper(" + ordem + ") like '" + filtro.toUpperCase() + "%' ";
            }
        }
        jpql += where;
        jpql += " order by " + ordem;
        
        totalObjetos = em.createQuery("select id from " + classePersistence.getSimpleName() + 
                                       where + " order by "+ordem).getResultList().size();
               
        return em.createQuery(jpql).
                setFirstResult(posicaoAtual).
                setMaxResults(maximoObjetos).getResultList();
    }
    
    public void primeiro(){
        posicaoAtual = 0;
    }
    
    public void anterior(){
        posicaoAtual -= maximoObjetos;
        if (posicaoAtual < 0 ) {
            posicaoAtual = 0;
        }
    }
    
  
    
    public void rollback(){
        if(em.getTransaction().isActive() == false){
            em.getTransaction().begin();
        }
        em.getTransaction().rollback();
       
    }
            
    
    public void proximo(){
        if (posicaoAtual + maximoObjetos < totalObjetos) {
            posicaoAtual += maximoObjetos;
        }
    }
    
    public void ultimo(){
        if (totalObjetos%maximoObjetos == 0) {
            posicaoAtual  = totalObjetos - maximoObjetos;     
        }else{
            do {
               totalObjetos++;  
            } while (totalObjetos%maximoObjetos != 0);
            posicaoAtual  = totalObjetos - maximoObjetos;
        }
    }
    
    public String getMensagemNavegacao(){
        int ate = posicaoAtual+maximoObjetos;
        if (ate > totalObjetos) {
            ate = totalObjetos;
        }
        return "Listando de " + (posicaoAtual + 1) + " ate " + ate + " de "+totalObjetos + " registros";
    }
    
    public boolean persist(T obj){
        try {
            em.getTransaction().begin();
            em.persist(obj);
            em.getTransaction().commit();
            mensagem = "Objeto persistido com sucesso";
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive() == false) {
                em.getTransaction().begin();
            }
            em.getTransaction().rollback();
            mensagem = "Erro ao persistir objeto";
                Util.getMensagemErro(e);
            return false;
        }
    }
    
     public boolean merge(T obj){
        try {
            em.getTransaction().begin();
            em.merge(obj);
            em.getTransaction().commit();
            mensagem = "Objeto persistido e atualizado com sucesso";
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive() == false) {
                em.getTransaction().begin();
            }
            em.getTransaction().rollback();
            mensagem = "Erro ao persistir objeto";
                Util.getMensagemErro(e);
            return false;
        }
    }
     
      public boolean remover(T obj){
        try {
            em.getTransaction().begin();
            em.remove(obj);
            em.getTransaction().commit();
            mensagem = "Objeto removido com sucesso";
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive() == false) {
                em.getTransaction().begin();
            }
            em.getTransaction().rollback();
            mensagem = "Erro ao remover objeto";
                Util.getMensagemErro(e);
            return false;
        }
    }
      
    public T localizar(Integer id){
        return (T) em.find(classePersistence, id);
    }

    public void setListaObjetos(List<T> listaObjetos) {
        this.listaObjetos = listaObjetos;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public Class getClassePersistence() {
        return classePersistence;
    }

    public void setClassePersistence(Class classePersistence) {
        this.classePersistence = classePersistence;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Integer getMaximoObjetos() {
        return maximoObjetos;
    }

    public void setMaximoObjetos(Integer maximoObjetos) {
        this.maximoObjetos = maximoObjetos;
    }

    public Integer getPosicaoAtual() {
        return posicaoAtual;
    }

    public void setPosicaoAtual(Integer posicaoAtual) {
        this.posicaoAtual = posicaoAtual;
    }

    public Integer getTotalObjetos() {
        return totalObjetos;
    }

    public void setTotalObjetos(Integer totalObjetos) {
        this.totalObjetos = totalObjetos;
    }

    public List<T> getListaTodos() {
        String jpql = "from "+classePersistence.getSimpleName() + " order by " + ordem;
        return  em.createQuery(jpql).getResultList();
    }

    public void setListaTodos(List<T> listaTodos) {
        this.listaTodos = listaTodos;
    }
    
    
}
