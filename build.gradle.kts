plugins {
    kotlin("jvm")
    id("dev.isxander.modstitch.base") version "0.5.+"
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.4.+"
}

fun prop(name: String) : String {
    return findProperty(name)?.toString() ?: throw IllegalArgumentException("Missing property: $name")
}

fun ifFindProperty(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

val minecraft = stonecutter.current.project.substringBeforeLast('-')

modstitch {
    minecraftVersion = minecraft

    val j21 = stonecutter.eval(minecraft, ">=1.20.6")
    javaTarget = if (j21) 21 else 17
    kotlin {
        jvmToolchain(if (j21) 21 else 17)
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        ifFindProperty("deps.parchment") { mappingsVersion = it }
    }

    // Fabric Loom (Fabric)
    loom {
        fabricLoaderVersion = prop("deps.fabric_loader")

        // Configure loom like normal in this block.
        configureLoom {
        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        enable {
            ifFindProperty("deps.forge") { forgeVersion = it }
            ifFindProperty("deps.neoforge") { neoForgeVersion = it }
        }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoforge {
            runs.all {
                disableIdeRun()
            }
        }
    }

    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = false

        configs.register("flightassistant")

        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }
}

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
val loader: String = name.split("-")[1]
stonecutter {
    consts(
        "fabric" to (loader == "fabric"),
        "neoforge" to (loader == "neoforge"),
        "forge" to (loader == "forge")
    )
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        ifFindProperty("deps.fapi") {
            if (stonecutter.current.isActive) {
                modstitchModLocalRuntime("net.fabricmc.fabric-api:fabric-api:$it")
            }
        }
        modstitchModImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}+kotlin.2.1.0")
    }

    modstitch.moddevgradle {
        modstitchModRuntimeOnly("thedarkcolour:kotlinforforge${if (modstitch.isModDevGradleRegular) "-neoforge" else ""}:${property("deps.kff")}")
    }

    // Anything else in the dependencies block will be used for all platforms.
    modstitchModImplementation("dev.architectury:architectury-${loader}:${property("deps.arch_api")}")
    modstitchModImplementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")

    // Other
    ifFindProperty("deps.compat") {
        for (dependency in it.split(',')) {
            val (group, modId, version) = dependency.split(':')
            if (version == "[NONE]") {
                stonecutter.consts[modId] = false
                continue
            }
            if (group == "maven.modrinth") {
                modstitchModCompileOnly("${group}:${modId}:${version}")
            }
            if (stonecutter.current.isActive) {
                modstitchModLocalRuntime("${group}:${modId}:${version}") {
                    exclude("net.fabricmc")
                }
            }
            stonecutter.consts[modId] = true
        }
    }
}

/*
// Variables
class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

val mod = ModData()

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val mcDep = property("mod.mc_dep").toString()
val isSnapshot = hasProperty("env.snapshot")

version = "${mod.version}+mc$minecraft"
group = mod.group
base { archivesName.set("${mod.id}-$loader") }

// Dependencies
repositories {

    strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
    strictMaven("https://maven.fallenbreath.me/releases", "me.fallenbreath")
    strictMaven("https://maven.isxander.dev/releases", "dev.isxander", "org.quiltmc.parsers")
    strictMaven("https://maven.su5ed.dev/releases", "org.sinytra", "org.sinytra.forgified-fabric-api")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    fun ifStable(str: String, action: (String) -> Unit = { modImplementation(it) }) {
        if (isSnapshot) modCompileOnly(str) else action(str)
    }

    minecraft("com.mojang:minecraft:${minecraft}")
    mappings(loom.officialMojangMappings())
    val mixinExtras = "io.github.llamalad7:mixinextras-%s:${property("deps.mixin_extras")}"
    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
        val fapi = property("deps.fapi")
        if (stonecutter.current.isActive && fapi != "[VERSIONED]") {
            modLocalRuntime("net.fabricmc.fabric-api:fabric-api:$fapi")
        }
        ifStable("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    } else {
        if (loader == "forge") {
            "forge"("net.minecraftforge:forge:${minecraft}-${property("deps.fml")}")
            compileOnly(annotationProcessor(mixinExtras.format("common"))!!)
            include(implementation(mixinExtras.format("forge"))!!)
        } else {
            "neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
        }

        "forgeRuntimeLibrary"("org.quiltmc.parsers:json:0.2.1")
        "forgeRuntimeLibrary"("org.quiltmc.parsers:gson:0.2.1")
    }

    // Config
    modImplementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}") {
        if (!isFabric) {
            isTransitive = false
        }
    }


}

// Resources
tasks.processResources {
    inputs.property("version", mod.version)
    inputs.property("mc", mcDep)

    val map = mapOf(
        "version" to mod.version,
        "mc" to mcDep,
        "fml" to if (loader == "neoforge") "1" else "45",
        "mnd" to if (loader == "neoforge") "" else "mandatory = true"
    )

    fun FileCopyDetails.expandOrExclude(expand: Boolean, map: Map<String, String>): Any = if (expand) expand(map) else exclude()

    filesMatching("fabric.mod.json") { expandOrExclude(loader == "fabric", map) }
    filesMatching("META-INF/mods.toml") { expandOrExclude(loader == "forge", map) }
    filesMatching("META-INF/neoforge.mods.toml") { expandOrExclude(loader == "neoforge", map) }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

// Publishing
publishMods {
    val modrinthToken = findProperty("modrinthToken")
    val curseforgeToken = findProperty("curseforgeToken")
    dryRun = modrinthToken == null || curseforgeToken == null

    file = tasks.remapJar.get().archiveFile
    additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
    displayName =
        "${mod.name} ${mod.version} for ${loader.replaceFirstChar { it.uppercase() }} ${property("mod.mc_title")}"
    version = "${mod.version}+mc$minecraft-$loader"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = ALPHA
    modLoaders.add(loader)
    if (isFabric) {
        modLoaders.add("quilt")
    }

    val targets = property("mod.mc_targets").toString().split(' ')
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken.toString()
        targets.forEach(minecraftVersions::add)
        if (isFabric) {
            requires("fabric-language-kotlin")
            optional("modmenu")
        } else {
            requires("kotlin-for-forge")
        }
        requires("architectury-api")
        requires("yacl")
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        if (isFabric) {
            requires("fabric-language-kotlin")
            optional("modmenu")
        } else {
            requires("kotlin-for-forge")
        }
        requires("architectury-api")
        requires("yacl")
    }
}
*/
