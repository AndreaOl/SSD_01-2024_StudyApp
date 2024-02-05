backend "consul" {
  address = "consul:8500"
  path    = "vault"
  scheme  = "http"
  ha_enabled  = false
}

listener "tcp" {
  address     = "vault:8200"
  tls_disable = true
}

default_lease_ttl = "168h"
max_lease_ttl     = "720h"
ui                = true
