class IClass < ApplicationRecord
  belongs_to :package

  has_many :i_methods, dependent: :destroy
  # has_many :attributes
  has_many :class_attributes, class_name: "Attribute", dependent: :destroy
end
