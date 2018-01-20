def recFileFind(File f, Set<File> set) {
    f.listFiles().each {
        if (it.isDirectory()) recFileFind(it, set) else set.add(it)
    }
}

Set<File> s = []

new File(".").listFiles().findAll { it.isDirectory() }.each { recFileFind(it, s) }

def c = 0
def b = 0

s.each {
    def l = it.readLines("UTF-8")
    c += l.size()
    l.removeIf { !it.isEmpty() }
    b += l.size()
}

println("$c lines of code ($b blank lines)")