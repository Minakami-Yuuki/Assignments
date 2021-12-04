//
// Created by Göksu Güvendiren on 2019-05-14.
//

#include "Scene.hpp"


void Scene::buildBVH() {
    printf(" - Generating BVH...\n\n");
    this->bvh = new BVHAccel(objects, 1, BVHAccel::SplitMethod::NAIVE);
}

Intersection Scene::intersect(const Ray &ray) const
{
    return this->bvh->Intersect(ray);
}

void Scene::sampleLight(Intersection &pos, float &pdf) const
{
    float emit_area_sum = 0;
    for (uint32_t k = 0; k < objects.size(); ++k) {
        if (objects[k]->hasEmit()){
            emit_area_sum += objects[k]->getArea();
        }
    }
    float p = get_random_float() * emit_area_sum;
    emit_area_sum = 0;
    for (uint32_t k = 0; k < objects.size(); ++k) {
        if (objects[k]->hasEmit()){
            emit_area_sum += objects[k]->getArea();
            if (p <= emit_area_sum){
                objects[k]->Sample(pos, pdf);
                break;
            }
        }
    }
}

bool Scene::trace(
        const Ray &ray,
        const std::vector<Object*> &objects,
        float &tNear, uint32_t &index, Object **hitObject)
{
    *hitObject = nullptr;
    for (uint32_t k = 0; k < objects.size(); ++k) {
        float tNearK = kInfinity;
        uint32_t indexK;
        Vector2f uvK;
        if (objects[k]->intersect(ray, tNearK, indexK) && tNearK < tNear) {
            *hitObject = objects[k];
            tNear = tNearK;
            index = indexK;
        }
    }


    return (*hitObject != nullptr);
}

// Implementation of Path Tracing
Vector3f Scene::castRay(const Ray &ray, int depth) const
{
    // TO DO Implement Path Tracing Algorithm here
    // Path Tracing:

    // First: judge whether have intersections:
    Intersection intersection = intersect(ray);

    // No intersections:
    if(!intersection.happened) {
        return Vector3f(0, 0, 0);
    }
    // Special: when the camera shoot the emit:
    // depth == 0: here is emit
    if(intersection.emit.norm() > 0) {
        if(depth == 0) {
            return intersection.emit;
        }
        else {
            return Vector3f(0, 0, 0);
        }
    }

    // define varieties:
    // p: the intersection
    // w0: ray direction (negative)
    // material: the intersection's material
    Vector3f& p = intersection.coords;
    Vector3f wo = normalize(-ray.direction);
    Vector3f normal = normalize(intersection.normal);
    Material*& material = intersection.m;

    // format: v < 0 --> v = 0
    auto format = [](Vector3f &a) {
        if(a.x < 0) a.x = 0;
        if(a.y < 0) a.y = 0;
        if(a.z < 0) a.z = 0;
    };

    // direct ray:
    Vector3f L_direct;
    {
        Intersection inter_dir;
        float pdf_dir;
        // calculate the (Probabily destiny)
        sampleLight(inter_dir, pdf_dir);

        Vector3f& x = inter_dir.coords;
        Vector3f ws = normalize(x - p);
        Vector3f light_normal = normalize(inter_dir.normal);

        // judge between the ray and the intersection 
        // whether is blocked by obj,
        // and the lighting surface is not the emit.
        auto rayNoblocked = intersect(Ray(p, ws));
        if(rayNoblocked.happened && (rayNoblocked.coords - x).norm() < 1e-2) {
            // direct ray formulation:
            // BRDF = (material)eval(wo, ws, Normal)
            // cos_theta_x: cal the normal of Area to the hemi-sphere surface
            // distance = |x - p| ^ 2
            L_direct = inter_dir.emit * material->eval(ws, wo, normal) * dotProduct(ws, normal) * dotProduct(-ws, light_normal) / (dotProduct((x-p), (x-p)) * pdf_dir);

            format(L_direct);
        }
    }

    // indirect ray:
    Vector3f L_indirect;
    {
        // Russian Roulette: (p in, 1 - p out)
        // p is random (get_random_float)
        float RR = this->RussianRoulette;
        
        // in: (hit a non-emitting obj at q)
        if(get_random_float() < RR) {
            Vector3f wi = normalize(material->sample(wo, normal));

            // (iterate)
            L_indirect = castRay(Ray(p, wi), depth + 1) * material->eval(wi, wo, normal) * dotProduct(wi, normal) / (material->pdf(wi, wo, normal) * RR);

            format(L_indirect);
        }
    }

    return L_direct + L_indirect;
}