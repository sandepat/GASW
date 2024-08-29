/* Copyright CNRS-CREATIS
 *
 * Rafael Ferreira da Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.insalyon.creatis.gasw.dao.hibernate;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import fr.insalyon.creatis.gasw.bean.Job;
import fr.insalyon.creatis.gasw.dao.DAOException;
import fr.insalyon.creatis.gasw.dao.JobDAO;
import fr.insalyon.creatis.gasw.execution.GaswStatus;

/**
 *
 * @author Rafael Ferreira da Silva
 */
public class JobData implements JobDAO {

    private static final Logger logger = Logger.getLogger(JobData.class);
    private SessionFactory sessionFactory;

    public JobData(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    @Override
    public void add(Job job) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(job);
            session.getTransaction().commit();
            session.close();

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public void update(Job job) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.merge(job);
            session.getTransaction().commit();
            session.close();

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public void remove(Job job) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(job);
            session.getTransaction().commit();
            session.close();

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public Job getJobByID(String id) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            Job job = (Job) session.getNamedQuery("Job.findById")
                    .setString("id", id)
                    .uniqueResult();
            session.getTransaction().commit();
            session.close();

            return job;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getActiveJobs() throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.getActive")
                    .setString("submitted", GaswStatus.SUCCESSFULLY_SUBMITTED.name())
                    .setString("queued", GaswStatus.QUEUED.name())
                    .setString("running", GaswStatus.RUNNING.name())
                    .setString("kill", GaswStatus.KILL.name())
                    .setString("replicate", GaswStatus.REPLICATE.name())
                    .setString("reschedule", GaswStatus.RESCHEDULE.name())
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getJobs(GaswStatus status) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.findByStatus")
                    .setString("status", status.name()).list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public long getNumberOfCompletedJobsByInvocationID(int invocationID) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            long completedJobs = (Long) session.getNamedQuery("Job.getCompletedJobsByInvocationID")
                    .setInteger("invocationID", invocationID)
                    .setString("completed", GaswStatus.COMPLETED.name())
                    .uniqueResult();
            session.getTransaction().commit();
            session.close();

            return completedJobs;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getActiveJobsByInvocationID(int invocationID) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.findActiveByInvocationID")
                    .setInteger("invocationID", invocationID)
                    .setString("submitted", GaswStatus.SUCCESSFULLY_SUBMITTED.name())
                    .setString("queued", GaswStatus.QUEUED.name())
                    .setString("running", GaswStatus.RUNNING.name())
                    .setString("kill", GaswStatus.KILL.name())
                    .setString("replicate", GaswStatus.REPLICATE.name())
                    .setString("reschedule", GaswStatus.RESCHEDULE.name())
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getFailedJobsByInvocationID(int invocationID) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.findFailedByInvocationID")
                    .setInteger("invocationID", invocationID)
                    .setString("error", GaswStatus.ERROR.name())
                    .setString("stalled", GaswStatus.STALLED.name())
                    .setString("error_held", GaswStatus.ERROR_HELD.name())
                    .setString("stalled_held", GaswStatus.STALLED_HELD.name())
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getRunningByCommand(String command) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.getRunningByCommand")
                    .setString("command", command)
                    .setString("running", GaswStatus.RUNNING.name())
                    .setString("kill", GaswStatus.KILL.name())
                    .setString("replicate", GaswStatus.REPLICATE.name())
                    .setString("reschedule", GaswStatus.RESCHEDULE.name())
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getCompletedByCommand(String command) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.getCompletedByCommand")
                    .setString("command", command)
                    .setString("completed", GaswStatus.COMPLETED.name())
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getByParameters(String parameters) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            System.out.println(parameters + "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            List<Job> list = (List<Job>) session.getNamedQuery("Job.findByParameters")
                    .setString("parameters", parameters)
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job> getFailedByCommand(String command) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.getFailedByCommand")
                    .setString("command", command)
                    .setString("error", GaswStatus.ERROR.name())
                    .setString("stalled", GaswStatus.STALLED.name())
                    .setString("error_held", GaswStatus.ERROR_HELD.name())
                    .setString("stalled_held", GaswStatus.STALLED_HELD.name())
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Job>  getJobsByCommand(String command) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Job> list = (List<Job>) session.getNamedQuery("Job.getJobsByCommand")
                    .setString("command", command)
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

    @Override
    public List<Integer> getInvocationsByCommand(String command) throws DAOException {

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            List<Integer> list = (List<Integer>) session.getNamedQuery("Job.getInvocationsByCommand")
                    .setString("command", command)
                    .list();
            session.getTransaction().commit();
            session.close();

            return list;

        } catch (HibernateException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }


}
