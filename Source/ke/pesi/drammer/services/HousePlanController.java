/*
 *  Bitforge Software Labs
 *  (c)2017 
 *  http://bitforge.co.ke
 */
package ke.pesi.drammer.services;

/**
 *
 * @project: ramani-digital
 * @author kelly
 *
 */
import javax.inject.Named;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ke.pesi.drammer.model.dao.HousePlan;
import ke.pesi.drammer.model.dao.Roofing;
import ke.pesi.drammer.model.dao.Roomcount;
import ke.pesi.drammer.model.dao.Typology;
import ke.pesi.drammer.model.facade.HousePlanFacade;
import ke.pesi.drammer.model.facade.RoofingFacade;
import ke.pesi.drammer.model.facade.RoomcountFacade;
import ke.pesi.drammer.model.facade.TypologyFacade;
import ke.pesi.drammer.services.util.FileUploadManager;
import ke.pesi.drammer.services.util.RandomDirNameGen;

@Named(value = "planController")
@ViewScoped
public class HousePlanController  {

    @EJB private HousePlanFacade housePlanFacade;
    @EJB private RoofingFacade roofingFacade;
    @EJB private TypologyFacade typologyFacade;
    @EJB private RoomcountFacade roomCountFacade;
    private HousePlan newPlan;
    private Roomcount numOfRooms;
    private Typology typology;
    private Roofing roof;
    private Boolean featuredState;
    private Map<String, Object> keyStore;
    private FileUploadManager uploadManager;
    private FacesContext context;
    private static final String IMG_DIR_KEY = "img.dir";
    private static final String DOC_DIR_KEY = "doc.dir";

    
    public HousePlanController() {
        context = FacesContext.getCurrentInstance();
        newPlan = new HousePlan();
        keyStore = new HashMap<>();
        typology = new Typology();
        numOfRooms = new Roomcount();
        roof = new Roofing();
    }

    /*
     * Check if page is from new request, if new remove old dir name and generate
     * new directory name and store name in context. If its a postback request 
     * retain the directory name stored.
     */
    @PostConstruct
    public void generatePlanDirNames() {
        if (context.isPostback()) {
            return;
        } 
    //new request...create directories
        if (keyStore.containsKey(IMG_DIR_KEY) && keyStore.containsKey(DOC_DIR_KEY)) {
            keyStore.clear();
        }
        keyStore.put(IMG_DIR_KEY, new RandomDirNameGen().getDirectoryName());
        keyStore.put(DOC_DIR_KEY, new RandomDirNameGen().getDirectoryName());
    }

    /*Create newPlan entity and associated entities and persist to db*/
    public String savePlan() {
        newPlan.setUploadDate(GregorianCalendar.getInstance().getTime());
        //TODO: Random 4 digit generator relate both: images and option files
        newPlan.setImgFilesetDir((String) keyStore.get(IMG_DIR_KEY));
        newPlan.setOptFilesetDir((String) keyStore.get(DOC_DIR_KEY));
        newPlan.setFeaturedState(convertBooleanToInt(featuredState));

        //get the ids of the selected roof and typology
        typology = typologyFacade.find(typology.getTypologyId());
        roof = roofingFacade.find(roof.getRoofingId());
        typology.getHousePlanCollection().add(newPlan);
        roof.getHousePlanCollection().add(newPlan);
        newPlan.setTypology(typology);
        newPlan.setRoof(roof);
        //create plan
        roomCountFacade.create(numOfRooms);
        newPlan.setRoomCount(numOfRooms);//room count?
        housePlanFacade.create(newPlan);
        context.addMessage(null, 
                new FacesMessage("Plan Saved at: #" + GregorianCalendar.getInstance().
                        getTime().toString()));
        return "product_list?faces-redirect=true";
    }

    public void saveAndPublishPlan() {
        housePlanFacade.create(newPlan);
    }

    /*View a listing of all plans*/
    public List<HousePlan> getAllPlans() {
        List<HousePlan> plans;
        plans = housePlanFacade.findAll();
        return plans;
    }

    public HousePlan getNewPlan() {
        return newPlan;
    }

    public void setNewPlan(HousePlan newPlan) {
        this.newPlan = newPlan;
    }

    public Roofing getRoofType() {
        return roof;
    }

    public void setRoofType(Roofing roofType) {
        this.roof = roofType;
    }

    public Roomcount getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(Roomcount numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    public Typology getTypology() {
        return typology;
    }

    public void setTypology(Typology typology) {
        this.typology = typology;
    }

//    public int convertFeaturedState(String state){
//        if("true".equals(state)) return 1;
//        else return 0;
//    }
    public Boolean getFeaturedState() {
        return featuredState;
    }

    public void setFeaturedState(Boolean featuredState) {
        this.featuredState = featuredState;
    }

    private int convertBooleanToInt(Boolean state) {
        return state ? 1 : 0;
    }

    public FileUploadManager getUploadManager() {
        return uploadManager;
    }

    public void setUploadManager(FileUploadManager uploadManager) {
        this.uploadManager = uploadManager;
    }

    public Map<String, Object> getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(Map c) {
        this.keyStore = c;
    }
    
    public FacesContext getContext() {
        return context;
    }

    public void setContext(FacesContext context) {
        this.context = context;
    }
    
}
