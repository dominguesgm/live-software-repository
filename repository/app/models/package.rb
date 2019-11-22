class Package < ApplicationRecord
    belongs_to :project
    
    has_many :i_classes, dependent: :destroy
end
