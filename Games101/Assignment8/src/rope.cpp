#include <iostream>
#include <vector>

#include "CGL/vector2D.h"

#include "mass.h"
#include "rope.h"
#include "spring.h"

namespace CGL {

    Rope::Rope(Vector2D start, Vector2D end, int num_nodes, float node_mass, float k, vector<int> pinned_nodes)
    {
        // TODO (Part 1): Create a rope starting at `start`, ending at `end`, and containing `num_nodes` nodes.

        // set current node's position (now), weight (node), status(whether pin == 0)
        for(int i = 0; i < num_nodes; i++) {
            Vector2D now = start + (end - start) * i / (num_nodes - 1);
            masses.push_back(new Mass(now, node_mass, 0));
        }

        // set the nodes are pinned (stationary)
        for (auto &i : pinned_nodes) {
           masses[i]->pinned = true;
        }

        // create a Spring between 2 contant nodes:
        for(int i = 0; i < num_nodes - 1; i++) {
            springs.push_back(new Spring(masses[i], masses[i + 1], k));
        }
    }

    void Rope::simulateEuler(float delta_t, Vector2D gravity)
    {
        for (auto &s : springs)
        {
            // TODO (Part 2): Use Hooke's law to calculate the force on a node
            // 2 nodes distance:
            auto ab = (s->m2->position - s->m1->position).norm();
            // 2 nodes direction:
            auto ab_dir = (s->m2->position - s->m1->position) / ab;

            // elastic F:
            // m1 is a, m2 is b (F is oppsited)
            Vector2D f_a = s->k * ab_dir * (ab - s->rest_length);
            s->m1->forces += f_a;
            s->m2->forces += -f_a;

        }

        // control the Euler whether explicit or not:
        bool is_explicit = false;

        for (auto &m : masses)
        {
            if (!m->pinned)
            {
                // TODO (Part 2): Add the force due to gravity, then compute the new velocity and position
                // calculate the sum of F:
                m->forces += gravity * m->mass;

                // TODO (Part 2): Add global damping
                // define the damping (TODO: prevent the Spring)
                // explicit Euler: (?)
                if(is_explicit) {
                    float k_d = 7;
                    m->forces += -k_d * m->velocity;
                }
                // implicite Euler:
                else {
                    float k_d = 0.01;
                    m->forces += -k_d * m->velocity;
                }

                // calculate the ex-im Euler:
                // a: acceleration
                auto a = m->forces / m->mass;
                // v: velocity (speed)
                auto v = m->velocity;
                // v + 1: the next position's velocity
                auto v_np = v + a * delta_t;
                
                // formulation:
                // explicit:
                if(is_explicit) {
                    m->velocity = m->velocity + a * delta_t;
                    m->position = m->position + v * delta_t;
                }
                // semi-implicit:
                else {
                    m->velocity = m->velocity + a * delta_t;
                    m->position = m->position + v_np * delta_t;
                }

            }

            // Reset all forces on each mass
            m->forces = Vector2D(0, 0);
        }
    }

    void Rope::simulateVerlet(float delta_t, Vector2D gravity)
    {
        for (auto &s : springs)
        {
            // TODO (Part 3): Simulate one timestep of the rope using explicit Verlet （solving constraints)
            // same Euler: set the elastic
            // set -1 == infinite
            if(s->k != -1) {
                auto ab = (s->m2->position - s->m1->position).norm();
                auto ab_dir = (s->m2->position - s->m1->position) / ab;

                Vector2D f_a = s->k * ab_dir * (ab - s->rest_length);
                s->m1->forces += f_a;
                s->m2->forces += -f_a;
            }
        }

        for (auto &m : masses)
        {
            if (!m->pinned)
            {
                // s->k != -1 (k == NOT infinite)

                // TODO (Part 3.1): Set the new position of the rope mass
                // set a:
                m->forces += gravity * m->mass;
                auto a = m->forces / m->mass;

                // set x_position: (last == previous)
                // from first to last: t0 t1 t2
                Vector2D x_t0 = m->last_position;
                Vector2D x_t1 = m->position;

                // TODO (Part 4): Add global Verlet damping
                // damping: (0.00005)
                float damping = 0.00005;
                Vector2D x_t2 = x_t1 + (1 - damping) * (x_t1 - x_t0) + a * delta_t * delta_t;

                // update postion:
                m->last_position = x_t1;
                m->position = x_t2;
            }

            // Reset all forces on each mass
            m->forces = Vector2D(0, 0);
        }

        // k == infinite:
        // dont calculate the Spring's elastic
        for(auto &s : springs) 
        {
            if(s->k != -1) {
                continue;
            }

            // nodes position:
            auto dis = s->m2->position - s->m1->position;
            auto dir = dis / dis.norm();

            // Spring original length:
            auto len = s->rest_length;

            // the change postion:
            // m1 moves to Left, m2 moves to Right
            auto offset_m1 = -0.5 * dir * (dis.norm() - len);
            auto offset_m2 = 0.5 * dir * (dis.norm() - len);

            // whether pinned ?
            // if pinned --> cant update opsition
            if(s->m1->pinned && s->m2->pinned) {
                continue;
            }

            // m1 is fixed, so m2 moves twice length
            if(s->m1->pinned) {
                offset_m2 *= 2;
                offset_m1 = Vector2D(0, 0);
            }
            // m2 is fixed, so m1 moves twice length
            if(s->m2->pinned) {
                offset_m1 *= 2;
                offset_m2 = Vector2D(0, 0);
            }

            // update m1, m2 position:
            s->m1->position += offset_m1;
            s->m2->position += offset_m2;
        }
    }
}
